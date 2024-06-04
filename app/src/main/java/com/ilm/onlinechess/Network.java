package com.ilm.onlinechess;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

    //Return true if there is conexion to internet
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
