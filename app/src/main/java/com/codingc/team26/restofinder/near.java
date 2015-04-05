package com.codingc.team26.restofinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.Location;
import android.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import android.location.Criteria;


import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.location.LocationManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class near extends FragmentActivity implements LocationSource {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private OnLocationChangedListener mListener;
    getUserLocation userLocation;
    SessionManagement session;
    ProgressBar pb;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        session = new SessionManagement(getApplicationContext());

        pb = ((ProgressBar)findViewById(R.id.pbnearby));
        pb.setVisibility(View.VISIBLE);
        setUpMapIfNeeded();

        ((Button)findViewById(R.id.filterbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter = new Intent(near.this, filter.class);
                startActivityForResult(filter, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            String cost = data.getExtras().getString("cost");
            String pureVeg = data.getExtras().getString("veg");
            String bar = data.getExtras().getString("bar");
            String cuisine = data.getExtras().getString("cuisine");
            String homedelivery = data.getExtras().getString("homedelivery");
            Log.w("Gourmand", "cost==>"+cost+"&veg==>"+pureVeg+"&bar==>"+bar+"&cuisine==>"+cuisine+"&homedelivery==>"+homedelivery);
            new searchNearby().execute(query, cost, pureVeg, bar, cuisine, homedelivery);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        userLocation.cancelTimer();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setUpMapIfNeeded();

        getUserLocation.LocationResult locationResult = new getUserLocation.LocationResult(){
            @Override
            public void gotLocation(final Location location){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(location != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude(),
                                    Toast.LENGTH_LONG).show();
                            if (mListener != null) {
                                mListener.onLocationChanged(location);
                            }
                            query = location.getLatitude() + "," + location.getLongitude();
                            if (mMap != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                            }
                            new searchNearby().execute(query);
                        }else{
                            Toast.makeText(getApplicationContext(), "GPS is disabled, Please enable it!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        userLocation = new getUserLocation();
        userLocation.getLocation(this, locationResult);
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.setMyLocationEnabled(true);
                mMap.setLocationSource(this);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.0293085,72.8425254),9));

                Log.w("Gourmand", "Calling map setup");
                setUpMap();
            }
        }
    }


    private void setUpMap()
    {
        mMap.setMyLocationEnabled(true);

        Log.w("Gourmand", "Map setup");

    }

    @Override
    public void activate(OnLocationChangedListener listener)
    {
        mListener = listener;
        Log.w("Gourmand", "loc changed listener activated");
    }

    @Override
    public void deactivate()
    {
        mListener = null;
    }

    private class searchNearby extends AsyncTask<String, Integer, JSONObject >{
        ArrayList<HashMap<String, String>> restaurants = new ArrayList<HashMap<String, String>>();

        @Override
        protected JSONObject doInBackground(String...params){

            String url = "http://104.131.56.170/nearby";
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email+":"+password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("location", params[0]));
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
            return json;

        }
        protected void onPostExecute(JSONObject json){
            mMap.clear();
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
                        Double Latitude=restaurant.getDouble("lat");
                        Double Longitude=restaurant.getDouble("lon");
                        double distance = restaurant.getDouble("distance");
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
                        p.put("lat",Latitude.toString());
                        p.put("lon",Longitude.toString());
                        if(distance < 1) {
                            distance = distance * 1000;
                            p.put("distance", String.format("%d m", (int)distance));
                        }else{
                            p.put("distance", String.format("%.2f km", distance));
                        }
                        restaurants.add(p);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Latitude,Longitude))
                                .title(name));
                        Log.w("Gourmand", "name ==> " + name + " distance==>" + String.format("%.2f m", distance));
                    }catch(JSONException e){
                        Log.w("Gourmand", "failed to parse jsonobject");
                    }
                }
                ListView listview = (ListView) findViewById(R.id.resultlist);



                ListAdapter adapter=new SimpleAdapter(near.this,restaurants,R.layout.list_v,new String[]{"name","rating", "distance"},new int[]{R.id.nameh,R.id.type, R.id.address_locality});

                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view,
                                            int position, long id) {
                        Intent Info=new Intent(near.this,information.class);
                        Info.putExtra("id", restaurants.get(position).get("id"));
                        startActivity(Info);
                    }

                });
            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }
        }

    }

}


