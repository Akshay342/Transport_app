package com.fujitsu.transport.transportapp.model;

import android.content.Context;

import com.fujitsu.transport.transportapp.util.Constants;
import com.fujitsu.transport.transportapp.util.MyPreferences;

import javax.inject.Inject;

public class Session {
    private Context context;

    @Inject
    public Session(Context context) {
        this.context = context;
    }

    public boolean isLoggedIn() {
        return MyPreferences.getInstance(context).getBoolean(Constants.SHARED_PREF_LOGGED_IN,false);
    }

    public void setLoggedIn(boolean loggedIn,String userType,String userId,String userName) {
        MyPreferences.getInstance(context).saveBoolean(Constants.SHARED_PREF_LOGGED_IN,loggedIn);
        MyPreferences.getInstance(context).saveString(Constants.SHARED_PREF_USER_TYPE,userType);
        MyPreferences.getInstance(context).saveString(Constants.SHARED_PREF_USER_ID,userId);
        MyPreferences.getInstance(context).saveString(Constants.SHARED_PREF_USER_NAME,userName);
    }

    public String getUserType() {
        return MyPreferences.getInstance(context).getString(Constants.SHARED_PREF_USER_TYPE,"");
    }

    public String getUserName() {
        return MyPreferences.getInstance(context).getString(Constants.SHARED_PREF_USER_NAME,"");
    }

    public String getUserId() {
        return MyPreferences.getInstance(context).getString(Constants.SHARED_PREF_USER_ID,"");
    }

    public void setTokenLocally(String token){
        MyPreferences.getInstance(context).saveString(Constants.SHARED_PREF_FCM_TOKEN,token);
    }

    public String getToken() {
        return MyPreferences.getInstance(context).getString(Constants.SHARED_PREF_FCM_TOKEN,"");
    }

    public void logout(){
        MyPreferences.getInstance(context).removeData(Constants.SHARED_PREF_USER_TYPE);
        MyPreferences.getInstance(context).removeData(Constants.SHARED_PREF_LOGGED_IN);
        MyPreferences.getInstance(context).removeData(Constants.SHARED_PREF_FCM_TOKEN);
        MyPreferences.getInstance(context).removeData(Constants.SHARED_PREF_USER_ID);
        MyPreferences.getInstance(context).removeData(Constants.SHARED_PREF_USER_NAME);
    }
}
