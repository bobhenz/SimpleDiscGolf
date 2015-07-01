package com.bobhenz.simplediscgolf;

import android.location.Location;

public interface DiscGolfLocationListener {
    public void onLocationChanged(Location location);
}