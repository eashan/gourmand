package com.codingc.team26.restofinder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class restaurant_reviews extends Activity {

    SessionManagement session;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_reviews);

        session = new SessionManagement(getApplicationContext());

        id = getParent().getIntent().getExtras().getString("id");
    }

    @Override
    protected void onResume(){
        super.onResume();
        new getRestaurantReviews().execute(id);
        Log.w("Gourmand", "onResume called");
    }

    private class getRestaurantReviews extends AsyncTask<String, Integer, JSONObject>{

        ArrayList<HashMap<String, String>> reviews = new ArrayList<HashMap<String, String>>();

        @Override
        protected JSONObject doInBackground(String...params){
            String url = "http://104.131.56.170/get-reviews";
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", params[0]));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            return json;
        }

        protected void onPostExecute(JSONObject json){
            reviews.clear();
            try{
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++){
                    try{
                        JSONObject details = data.getJSONObject(i);
                        String name = details.getString("name");
                        String review = details.getString("review");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("review", review);
                        reviews.add(map);
                        Log.w("Gourmand", "name==>"+ name + "review==>" + review);

                    }catch(Exception JSONException){

                    }
                }
                ListView listview = (ListView) findViewById(R.id.restaurant_reviews);
                ListAdapter adapter=new SimpleAdapter(restaurant_reviews.this,reviews,R.layout.user_review,new String[]{"name","review"},new int[]{R.id.reviewer,R.id.review});
                listview.setAdapter(adapter);
            }catch(Exception JSONException){

            }
        }

    }

}
