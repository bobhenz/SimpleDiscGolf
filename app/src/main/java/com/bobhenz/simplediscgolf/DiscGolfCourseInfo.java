package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfCourseInfo {
    private long mDbId;
    private String mName;
    private List<DiscGolfHoleInfo> mHoleArray = new ArrayList<DiscGolfHoleInfo>();

    public DiscGolfCourseInfo(String name) {
        mDbId = -1;
        mName = name;
    }

    public long getDbId() {
        return mDbId;
    }

    public void setDbId(long dbId) {
        mDbId = dbId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<DiscGolfHoleInfo> getHoleList() {
        return mHoleArray;
    }

    public void addHole(DiscGolfHoleInfo holeInfo) {
        mHoleArray.add(holeInfo);
    }

    DiscGolfHoleInfo guessHole(Location location) {
        if (location == null) {
            // TODO: Return hole "1" if exists.
            // Else, Return hole "10" if exists.
            // Else, return "any" that exists.
        } else {
            // TODO: Find the nearest hole in this course
            // that matches this location and return it.
        }
        // If there are no holes yet associated with this course,
        // let's create one using default values.
        //TODO: Read settings here for defaults (e.g. par).
        DiscGolfHoleInfo hole = new DiscGolfHoleInfo("1", 3);
        mHoleArray.add(hole);
        return hole;
    }
}
