package com.example.stackapp.utils.extension;

import android.content.Context;
import android.content.res.Resources;

public class ResourceHelper {


    public static int dpToPx(int dp){
        return (int) (dp* Resources.getSystem().getDisplayMetrics().density);
    }
}
