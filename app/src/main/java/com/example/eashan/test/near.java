package com.example.eashan.test;

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
import com.google.android.gms.maps.model.LatLngBounds;
import android.location.Criteria;


import android.util.Log;
import android.widget.Toast;
import android.location.LocationManager;


public class near extends FragmentActivity implements LocationListener,LocationSource {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        }
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.0293085,72.8425254),9));



            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(10, 10))
                    .title("Hello world"));

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     private void setUpMapIfNeeded() {
     // Do a null check to confirm that we have not already instantiated the map.
     if (mMap == null)
     {
     // Try to obtain the map from the SupportMapFragment.
     mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.basicMap)).getMap();
     // Check if we were successful in obtaining the map.

     if (mMap != null)
     {
     setUpMap();
     }

     //This is how you register the LocationSource
     mMap.setLocationSource(this);
     }
     }

     /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
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

    @Override
    public void onLocationChanged(Location location)
    {
        Log.w("Gourmand","Location changed");
        if( mListener != null )
        {
            mListener.onLocationChanged( location );

            LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;

            if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
            {
                //Move the camera to the user's location once it's available!
                Toast.makeText(this, "Lat="+location.getLatitude()+" Long:"+location.getLongitude()
                        , Toast.LENGTH_SHORT).show();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }
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
}

