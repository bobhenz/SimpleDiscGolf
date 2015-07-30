package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfHoleData {
    private List<Location> mStrokeArray = new ArrayList<Location>();

    private DiscGolfHoleInfo mHoleInfo;

    public DiscGolfHoleData(DiscGolfHoleInfo holeInfo) {
        mHoleInfo = holeInfo;
    }

    public void addStroke(Location location) {
        mStrokeArray.add(location);
    }

    public void removeStroke(int stroke) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            mStrokeArray.remove(stroke - 1);
        }
    }

    public Location getStrokeLocation(int stroke) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            return mStrokeArray.get(stroke - 1);
        } else {
            return null;
        }
    }

    public void setStrokeLocation(int stroke, Location location) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            mStrokeArray.set(stroke - 1, location);
        }
    }

    public int getStrokeCount() {
        return mStrokeArray.size();
    }

    public DiscGolfHoleInfo getInfo() {
        return mHoleInfo;
    }

}
