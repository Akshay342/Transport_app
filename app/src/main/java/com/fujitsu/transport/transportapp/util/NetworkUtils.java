package com.fujitsu.transport.transportapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fujitsu.transport.transportapp.R;


public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            } else {
                CommonUtils.showToast(context, context.getString(R.string.cant_connect_internet));
                return false;
            }
        }
    }
}
