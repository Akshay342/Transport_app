package com.fujitsu.transport.transportapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class LocationMangement {

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "LocationPref";
    private static final String IS_LOCATION_AVAILBLE = "islocationavailble";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


    // Constructor
    public LocationMangement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void SaveLocation(String lat, String lon){
     //   editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_LATITUDE, lat);
        editor.putString(KEY_LONGITUDE, lon);
        editor.commit();
    }

    /**
     * Check getLoginMutableLiveData method wil check user getLoginMutableLiveData status
     * If false it will redirect user to getLoginMutableLiveData page
     * Else won't do anything
     * */
//    public void checkLogin(){
//        // Check getLoginMutableLiveData status
//        if(!this.isLoggedIn()){
//
////            Intent i = new Intent(_context, LoginActivity.class);
////            // Closing all the Activities
////            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////
////            // Add new Flag to start new Activity
////            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////
////            // Staring Login Activity
////            _context.startActivity(i);
//        }
//
//    }
    public HashMap<String, String> getLocationDetails(){
        HashMap<String, String> location = new HashMap<String, String>();
        location.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, null));
        location.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, null));
        return location;
    }

    public void clearLocation(){
        editor.clear();
        editor.commit();

//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(_context, LoginActivity.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        // Staring Login Activity
//        _context.startActivity(i);
    }

    /**
     * Quick check for getLoginMutableLiveData
     * **/
    // Get Login State
    public boolean isDataSaved(){
        return pref.getBoolean(IS_LOCATION_AVAILBLE, false);
    }
}