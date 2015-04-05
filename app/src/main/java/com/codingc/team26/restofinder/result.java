package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.codingc.team26.restofinder.card.BaseInflaterAdapter;
import com.codingc.team26.restofinder.card.CardItemData;
import com.codingc.team26.restofinder.card.IAdapterViewInflater;
public class result extends Activity {

    String query, type;
    SessionManagement session;
    ArrayList<HashMap<String, String>>restaurants=new ArrayList<HashMap<String, String>>();
    private ProgressBar pb;
    private static final int REQUEST_FILTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        pb=(ProgressBar)findViewById(R.id.pbresult);
        pb.setVisibility(View.GONE);
        session = new SessionManagement(getApplicationContext());
        query = getIntent().getExtras().getString("query");
        type = getIntent().getExtras().getString("type");
        Log.w("Gourmand", "Received query ====> " + query);
        setTitle(query);
        pb.setVisibility(View.VISIBLE);
        new searchRestaurants().execute(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            Intent filter = new Intent(result.this, filter.class);
            if(type.equals("cuisine")){
                filter.putExtra("cuisine", query);
            }
            startActivityForResult(filter, REQUEST_FILTER);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_FILTER && resultCode == RESULT_OK){
            String cost = data.getExtras().getString("cost");
            String pureVeg = data.getExtras().getString("veg");
            String bar = data.getExtras().getString("bar");
            String cuisine = data.getExtras().getString("cuisine");
            String homedelivery = data.getExtras().getString("homedelivery");
            Log.w("Gourmand", "cost==>"+cost+"&veg==>"+pureVeg+"&bar==>"+bar+"&cuisine==>"+cuisine+"&homedelivery==>"+homedelivery);
            if(type.equals("cuisine")){
                if(!cuisine.equals("All Cuisines")) {
                    query = cuisine;
                }
            }
            new searchRestaurants().execute(query, cost, pureVeg, bar, cuisine, homedelivery);
        }
    }

    private class searchRestaurants extends AsyncTask<String, Integer, JSONObject >{
        ArrayList<HashMap<String, String>> restaurants = new ArrayList<HashMap<String, String>>();

        @Override
        protected JSONObject doInBackground(String...params){

            String url = "http://104.131.56.170/";
            if(type.equals("restaurant")){
                url += "search-restaurants";
            }else if(type.equals("cuisine")){
                url += "search-by-cuisine";
            }else if(type.equals("location")){
                url += "search-by-location";
            }
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email+":"+password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("q", params[0]));
            if(params.length != 1){
                if(!params[1].equals("no")) {
                    nameValuePairs.add(new BasicNameValuePair("cost", params[1]));
                }
                if(!params[2].equals("no")){
                    nameValuePairs.add(new BasicNameValuePair("veg", params[2]));
                }
                if(!params[3].equals("no")){
                    nameValuePairs.add(new BasicNameValuePair("bar", params[3]));
                }
                if(!params[4].equals("All Cuisines")){
                    nameValuePairs.add(new BasicNameValuePair("cuisine", params[4]));
                }
                if(!params[5].equals("no")){
                    nameValuePairs.add(new BasicNameValuePair("homedelivery", "yes"));
                }
            }
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            Log.w("Gourmand", "url===>" + url + "?" + paramsString);
            return json;
        }

        protected void onPostExecute(JSONObject json){
            restaurants.clear();
            pb.setVisibility(View.GONE);
            try {
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++){
                    try{
                        JSONObject restaurant = data.getJSONObject(i);
                        String name = restaurant.getString("name");
                        String address_locality = restaurant.getString("address_locality");
                        Integer id = restaurant.getInt("id");
                        Double rating = restaurant.getDouble("rating");
                        HashMap<String,String> p;
                        p = new HashMap<String,String>();
                        p.put("name",name);
                        p.put("address_locality",address_locality);
                        p.put("id",Integer.toString(id));
                        if(Double.toString(rating).equals("0.0")){
                            p.put("rating", "-");
                        }else {
                            p.put("rating", Double.toString(rating));
                        }
                        restaurants.add(p);


                        Log.w("Gourmand", "name ==> " + name + " address ===>" + address_locality + " rating ===> " + rating + " id ===> " + id);
                    }catch(JSONException e){
                        Log.w("Gourmand", "failed to parse jsonobject");
                    }
                }
                ListView list = (ListView) findViewById(R.id.resultlist);
                list.addHeaderView(new View(this));
                list.addFooterView(new View(this));
                BaseInflaterAdapter<CardItemData> adapter = new BaseInflaterAdapter<CardItemData>(new CardInflater());

                ListAdapter adapter=new SimpleAdapter(result.this,restaurants,R.layout.list_v,new String[]{"name","rating", "address_locality"},new int[]{R.id.nameh,R.id.type, R.id.address_locality});
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                        HashMap<String, String> mp = new HashMap<String, String>();
                        mp = restaurants.get(position);
                        Intent Info=new Intent(result.this,information.class);
                        Info.putExtra("id", mp.get("id"));
                        startActivity(Info);
                    }

                });
            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }






        }

    }

}
