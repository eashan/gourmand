package com.codingc.team26.restofinder;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class restaurant_map extends FragmentActivity implements LocationListener,LocationSource {
    String id;
    SessionManagement session;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationSource.OnLocationChangedListener mListener;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);
        session = new SessionManagement(getApplicationContext());
        id = getParent().getIntent().getExtras().getString("id");
        Log.w("Gourmand", "id==>" + id);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        Button getdirections=(Button)findViewById(R.id.getdirections);
        getdirections.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?   saddr=" + session.getUserlat() + "," + session.getUserlon() + "&daddr=" + session.getrestaurantlat() + "," + session.getrestaurantlon()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER );
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }});
        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_FINE);
        String provider;
        provider = locationManager.getBestProvider(req,true);
        if(locationManager != null)
        {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(gpsIsEnabled)
            {
                Log.w("Gourmand", "GPS enabled=true");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
            }
            else if(networkIsEnabled)
            {
                Log.w("Gourmand", "Network enabled");

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            }
            else
            {
                Log.w("Gourmand", "Location service disabled");

                //Show an error dialog that GPS is disabled...
            }
        }
        else
        {
            //Show some generic error dialog because something must have gone wrong with location manager.
        }

        setUpMapIfNeeded();
        new restaurantfind().execute(id);

    }
    @Override
    public void onPause()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setUpMapIfNeeded();

        if(locationManager != null)
        {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
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


                Log.w("Gourmand", "Calling map setup");
                setUpMap();
            }

            mMap.setLocationSource(this);


        }
    }

    private void setUpMap()
    {
        mMap.setMyLocationEnabled(true);

        Log.w("Gourmand", "Map setup");

    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener)
    {
        mListener = listener;
        Log.w("Gourmand", "loc changed listener activated");
    }

    @Override
    public void deactivate()
    {
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location)
    {

        if( mListener != null )
        { Log.w("Gourmand","Location changed");
            mListener.onLocationChanged( location );

            //Move the camera to the user's location once it's available!
            Toast.makeText(this, "Lat=" + location.getLatitude() + " Long:" + location.getLongitude()
                    , Toast.LENGTH_SHORT).show();
            Log.w("Gourmand","Location changed :lat:"+location.getLatitude()+"long:"+location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            session.nearbycoordinates(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));


        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // TODO Auto-generated method stub
        Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
        Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
    }

    private class restaurantfind extends AsyncTask<String, Integer, JSONObject > {

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
                Double lat = location.getDouble("lat");
                Double lon = location.getDouble("lon");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lon))
                        .title(session.getcurrest()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 14));



                Log.w("Gourmand", "lat ==> " + lat + " lon ===>" + lon);

            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }

        }

    }

}
