package com.mrgames13.jimdo.colorconverter.image.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Patterns
import android.webkit.URLUtil

class NetworkTools(context: Context) {

    // Variables as objects
    private val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun isUrlValid(url: String): Boolean {
        return URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches()
    }

    fun isInternetAvailable(): Boolean {
        val ni = cm.activeNetworkInfo
        return ni != null && ni.isConnectedOrConnecting
    }
}