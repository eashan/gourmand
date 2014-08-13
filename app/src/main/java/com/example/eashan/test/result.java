package com.example.eashan.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class result extends Activity {

    String query;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        session = new SessionManagement(getApplicationContext());
        query = getIntent().getExtras().getString("query");
        Log.w("Gourmand", "Received query ====> " + query);
        ((EditText) findViewById(R.id.search)).setText(query);
        new searchRestaurants().execute(query);
    }

    private class searchRestaurants extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>>{

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String...params){

            String url = "http://14.97.152/197/gourmand/search-restaurants";
            HttpClient client = new DefaultHttpClient();
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email+":"+password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("q", query));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();

            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            ArrayList<HashMap<String, String>> restaurants = new ArrayList<HashMap<String, String>>();
            try {
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++){
                    try{
                        JSONObject restaurant = data.getJSONObject(i);
                        String name = restaurant.getString("name");
                        String address_locality = restaurant.getString("address_locality");
                        Integer id = restaurant.getInt("id");
                        Double rating = restaurant.getDouble("rating");
                        Log.w("Gourmand", "name ==> " + name + " address ===>" + address_locality + " rating ===> " + rating + " id ===> " + id);
                    }catch(JSONException e){
                        Log.w("Gourmand", "failed to parse jsonobject");
                    }
                }
            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }
            HttpGet httpGet = new HttpGet(url);

            return null;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
