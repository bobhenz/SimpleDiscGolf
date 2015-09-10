package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfHoleData {
    private List<DiscGolfStrokeData> mStrokeArray = new ArrayList<>();
    private Location mStartLocation;
    private long mDbId;
    private long mHoleInfoDbId;

    public long getDbId() { return mDbId; }

    public DiscGolfHoleData(long holeInfoDbId) {
        mDbId = -1;
        mHoleInfoDbId = holeInfoDbId;
    }

    public void addStroke(Location location, boolean isPenalty) {
        mStrokeArray.add(new DiscGolfStrokeData(location, isPenalty));
    }

    public void addStroke(DiscGolfStrokeData stroke) {
        assert(stroke != null);
        mStrokeArray.add(stroke);
    }

    public void removeStroke(int stroke) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            mStrokeArray.remove(stroke - 1);
        }
    }

    public void setStartLocation(Location location) {
        mStartLocation = location;
    }

    public Location getStartLocation() {
        return mStartLocation;
    }

    public Location getStrokeLocation(int stroke) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            return mStrokeArray.get(stroke - 1).getLocation();
        } else {
            return null;
        }
    }

    public void setStrokeLocation(int stroke, Location location) {
        if ((mStrokeArray.size() >= stroke) && (stroke > 0)) {
            mStrokeArray.get(stroke - 1).setLocation(location);
        }
    }

    public int getStrokeCount() {
        return mStrokeArray.size();
    }

    public long getHoleInfoDbId() {
        return mHoleInfoDbId;
    }

    public void setHoleInfoDbId(long dbId) {
        mHoleInfoDbId = dbId;
    }
}
