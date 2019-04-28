package com.example.stackapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.example.stackapp.R;

public class TaskHelper {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void openCustomChromeTab(Context context, String url) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setToolbarColor(context.getResources().getColor(R.color.blue_dark))
                .setShowTitle(true)
                .build();
        intent.launchUrl(context, Uri.parse(url));
    }
}
