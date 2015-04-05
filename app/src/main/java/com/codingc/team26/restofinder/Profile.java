package com.codingc.team26.restofinder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Profile extends Activity {

    SessionManagement session;
    String email;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManagement(getApplicationContext());

        String name = session.getUserDetails().get("name");
        email = session.getUserDetails().get("email");

        TextView user_name = (TextView)findViewById(R.id.username);
        user_name.setText(name);

        pb = (ProgressBar)findViewById(R.id.pbprofile);
        pb.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume(){
        super.onResume();
        new getUserReviews().execute(email);
        Log.w("Gourmand", "onResume called");
    }

    private class getUserReviews extends AsyncTask<String, Integer, JSONObject>{
        ArrayList<HashMap<String, String>> reviews = new ArrayList<HashMap<String, String>>();

        @Override
        protected JSONObject doInBackground(String...params) {
            String url = "http://104.131.56.170/get-reviews";
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            return json;
        }

        protected void onPostExecute(JSONObject json) {
            reviews.clear();
            pb.setVisibility(View.GONE);
            try{
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++){
                    try{
                        JSONObject details = data.getJSONObject(i);
                        String restaurant = details.getString("restaurant");
                        String review = details.getString("review");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("restaurant", restaurant);
                        map.put("review", review);
                        reviews.add(map);

                        Log.w("Gourmand", "restaurant==>" + restaurant + "&review=>"+review);

                    }catch(Exception JSONException){
                        Log.w("Gourmand", "failed to parse details");
                    }
                }
                ListView listview = (ListView) findViewById(R.id.user_reviews);
                ListAdapter adapter=new SimpleAdapter(Profile.this,reviews,R.layout.restaurant_review,new String[]{"restaurant","review"},new int[]{R.id.restaurantname,R.id.restaurantreview});
                listview.setAdapter(adapter);

            }catch(Exception JSONException){
                Log.w("Gourmand", "failed to parse data");
            }
        }

    }

}
