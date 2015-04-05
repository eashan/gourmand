package com.codingc.team26.restofinder;

/**
 * Created by eashan on 13/8/14.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManagement {
    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "GourmandPref";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_PASSWORD = "password";

    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        Log.w("Gourmand", "user added ==> name =" + name + "email = " + email + "password = " + password);
        editor.commit();
    }

    public void currentrestaurant(String name,String address, double cost,double rating, double lat,double lon){
        editor.putString("currrestaurant",name);
        editor.putString("restaddress",address);
        editor.putString("cost", Double.toString(cost));
        editor.putString("rating", Double.toString(rating));
        editor.putString("rlat",Double.toString(lat));
        editor.putString("rlon",Double.toString(lon));
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void nearbycoordinates(String Lat,String Long){
        editor.putString("lat",Lat);
        editor.putString("lon",Long);
        editor.commit();

    }

    public String getrestaurantlat(){
        return pref.getString("rlat",null);
    }

    public String getrestaurantlon(){
        return pref.getString("rlon",null);
    }

    public String getUserlat(){
        return pref.getString("lat",null);

    }
    public String getUserlon(){
        return pref.getString("lon",null);

    }

    public String getCoordinates()
    {
       return  pref.getString("lat",null)+pref.getString("lon",null);
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, register.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }
    public String getcurrest()
    {
        return pref.getString("currrestaurant",null);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        return user;
    }

}
