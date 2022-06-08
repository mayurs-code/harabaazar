package com.example.harabazar.Utilities;

import android.content.Context;
import android.location.LocationManager;

import androidx.core.location.LocationManagerCompat;

public class CheckLocation {
    public static boolean isLocationEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && LocationManagerCompat.isLocationEnabled(manager);
    }
}
