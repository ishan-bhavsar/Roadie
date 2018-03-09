package com.example.gptsi.roadie.Util;

import android.location.Location;

/**
 * Created by GPTSI on 14-01-2018.
 */

public interface locationServicesCallback {
    // This is just a regular method so it can return something or
    // take arguments if you like.
    public void onLocationChanged(Location location);
}
