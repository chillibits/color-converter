package com.mrgames13.jimdo.colorconverter.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URL;

public class NetworkUtils {

    //Konstanten

    //Variablen als Objekte
    private ConnectivityManager cm;

    //Variablen

    public NetworkUtils(Context context) {
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isUrl(String url_string) {
        try{
            new URL(url_string);
        } catch (Exception e) { return false; }
        return true;
    }

    public boolean isInternetAvailable() {
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
