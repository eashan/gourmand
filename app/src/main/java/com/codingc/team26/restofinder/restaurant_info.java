package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class restaurant_info extends Activity {

    String id;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        session = new SessionManagement(getApplicationContext());
        id = getParent().getIntent().getExtras().getString("id");
        Log.w("Gourmand", "id==>"+id);
        new searchRestaurants().execute(id);

    }

    private class searchRestaurants extends AsyncTask<String, Integer, JSONObject > {

        @Override
        protected JSONObject doInBackground(String...params){

            String url = "http://104.131.56.170/info";
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", params[0]));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            Log.w("Gourmand", "url===>" + url + "?" + paramsString);

            return json;

        }
        protected void onPostExecute(JSONObject json){
            try {
                JSONObject data = json.getJSONObject("data");
                JSONObject location=data.getJSONObject("location");
                JSONArray cuisine=data.getJSONArray("cuisines");
                JSONArray phone = data.getJSONArray("phone_numbers");
                String name = data.getString("name");
                String address = data.getString("address");
                Integer id = data.getInt("id");
                Double rating = data.getDouble("rating");
                String highlights = data.getString("highlights");
                float rating1=Math.round(rating);
                Integer cost = data.getInt("cost");
                Double lat=location.getDouble("lat");
                Double lon=location.getDouble("lon");
                String cuisines = "";
                for(int i = 0; i < cuisine.length(); i++){
                    cuisines += cuisine.getString(i) + "\n";
                }
                cuisines = cuisines.trim();
                final String phone_number;
                if(phone.length() > 0){
                    phone_number="tel:"+phone.getString(0);
                }else{
                    phone_number="tel:";
                }
                String opening_hours = data.getString("opening_hours");
                session.currentrestaurant(name,address,cost,rating,lat,lon);

                TextView rname=(TextView)findViewById(R.id.restaurantname);
                rname.setText(name);
                getParent().setTitle(name);
                TextView rcuisine=(TextView)findViewById(R.id.cuisinebtn);
                rcuisine.setText(cuisines);
                TextView rcost=(TextView)findViewById(R.id.rcost);
                rcost.setText("Rs. " + Integer.toString(cost) + " for two people (approx.)");
                TextView ropen=(TextView)findViewById(R.id.openinghours);
                ropen.setText(opening_hours);
                TextView raddress=(TextView)findViewById(R.id.raddress);
                raddress.setText(address);
                ((TextView)findViewById(R.id.highlights)).setText(highlights);
                Button call = (Button)findViewById(R.id.call);
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(phone_number));
                        startActivity(callIntent);
                    }
                });
                Log.w("Gourmand", "name ==> " + name + " address ===>" + address + " rating ===> " + rating + " id ===> " + id + " cost ==> " + cost + " opening_hours==> " + opening_hours);

            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }

        }

    }

}
