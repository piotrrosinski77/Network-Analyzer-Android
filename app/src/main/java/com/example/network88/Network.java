package com.example.network88;

import static androidx.core.content.ContextCompat.getSystemService;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
