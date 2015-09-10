package com.bobhenz.simplediscgolf;

import android.location.Location;

/**
 * Created by bhenz on 9/9/2015.
 */
public class DiscGolfStrokeData {
    private Location mFinalLocation;
    private boolean mIsPenalty;

    DiscGolfStrokeData(Location location, boolean isPenalty) {
        mFinalLocation = location;
        mIsPenalty = isPenalty;
    }

    public Location getLocation() {
        return mFinalLocation;
    }

    public void setLocation(Location location) {
        mFinalLocation = location;
    }
}
