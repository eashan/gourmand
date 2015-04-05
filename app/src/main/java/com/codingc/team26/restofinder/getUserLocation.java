package com.codingc.team26.restofinder;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anish on 9/9/14.
 */
public class getUserLocation {
    Timer timer;
    LocationManager locationManager;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean passive_enabled = false;

    public boolean getLocation(Context context, LocationResult result){
        locationResult = result;
        if(locationManager == null){
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        }
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch  (Exception e){

        }
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){

        }
        try{
            passive_enabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        }catch (Exception e){

        }
        if(!gps_enabled && !network_enabled && !passive_enabled){
            return false;
        }
        if(gps_enabled){
            Log.w("Gourmand", "GPS Enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }
        if(network_enabled){
            Log.w("Gourmand", "Network Enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerNetwork);
        }
        if(passive_enabled){
            Log.w("Gourmand", "Passive Enabled");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerPassive);
        }
        timer = new Timer();
        timer.schedule(new getLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
            Log.w("Gourmand", "Latitude:"+location.getLatitude()+" & Longitude:" + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
            Log.w("Gourmand", "Latitude:"+location.getLatitude()+" & Longitude:" + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    LocationListener locationListenerPassive = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerPassive);
            Log.w("Gourmand", "Latitude:"+location.getLatitude()+" & Longitude:" + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    class getLastLocation extends TimerTask{
        @Override
        public void run(){
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerPassive);
            Location gps_location = null, network_location = null, passive_location = null;
            if(gps_enabled){
                gps_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(network_enabled){
                network_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(passive_enabled){
                passive_location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            if(gps_location != null && network_location != null && passive_location != null){
                if(gps_location.getTime() > network_location.getTime()){
                    if(gps_location.getTime() > passive_location.getTime()){
                        Log.w("Gourmand", "Latitude:"+gps_location.getLatitude()+" & Longitude:" + gps_location.getLongitude());
                        locationResult.gotLocation(gps_location);
                    }else{
                        Log.w("Gourmand", "Latitude:"+passive_location.getLatitude()+" & Longitude:" + passive_location.getLongitude());
                        locationResult.gotLocation(passive_location);
                    }
                }else{
                    if(network_location.getTime() > passive_location.getTime()){
                        Log.w("Gourmand", "Latitude:"+network_location.getLatitude()+" & Longitude:" + network_location.getLongitude());
                        locationResult.gotLocation(network_location);
                    }else{
                        Log.w("Gourmand", "Latitude:"+passive_location.getLatitude()+" & Longitude:" + passive_location.getLongitude());
                        locationResult.gotLocation(passive_location);
                    }
                }
                return;
            }
            if(gps_location != null){
                Log.w("Gourmand", "Latitude:"+gps_location.getLatitude()+" & Longitude:" + gps_location.getLongitude());
                locationResult.gotLocation(gps_location);
                return;
            }
            if(network_location != null){
                Log.w("Gourmand", "Latitude:"+network_location.getLatitude()+" & Longitude:" + network_location.getLongitude());
                locationResult.gotLocation(network_location);
                return;
            }
            if(passive_location != null){
                Log.w("Gourmand", "Latitude:"+passive_location.getLatitude()+" & Longitude:" + passive_location.getLongitude());
                locationResult.gotLocation(passive_location);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public void cancelTimer(){
        timer.cancel();
        locationManager.removeUpdates(locationListenerGps);
        locationManager.removeUpdates(locationListenerNetwork);
        locationManager.removeUpdates(locationListenerPassive);
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }

}
