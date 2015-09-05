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
    /**
     * The "null" course is the one that always exists which is never modified
     * and which is chosen when the user's location is unknown. The course
     * itself is never written into the database, however the "null" course does
     * have some preset holes associated with it that are in the database.
     * Doing so allows game data to be associated with hole information, even
     * when using the null-course. It also has other conveniences such as
     * allowing us to present the user with a list of holes to select from,
     * even if they are using the null-course.
     */
    public static final String NULL_COURSE_NAME = "Default";
    public static final long NULL_COURSE_DB_ID = -1;
    private boolean mIsNull;

    public DiscGolfCourseInfo(String name) {
        mDbId = -1;
        mIsNull = false;
        mName = name;
    }

    public void setIsNull(boolean isDefault) {
        mIsNull = isDefault;
    }

    public boolean getIsNull() {
        return mIsNull;
    }

    public long getDbId() {
        return mDbId;
    }

    public void setDbId(long dbId) {
        mDbId = dbId;
        // Touch up all the holes to continue pointing
        // to this course.
        for (DiscGolfHoleInfo holeInfo : mHoleArray) {
            holeInfo.setCourseDbId(mDbId);
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        //TODO: If we are NULL then I would like to write a new entry to the database. Should I do that within this function? I don't have the database here...
        mName = name;
    }

    public List<DiscGolfHoleInfo> getHoleList() {
        return mHoleArray;
    }

    public void addHole(DiscGolfHoleInfo holeInfo) {
        // similar need to handle "NULL" scenario here...
        holeInfo.setCourseDbId(mDbId);
        mHoleArray.add(holeInfo);
    }

    public void removeHole(DiscGolfHoleInfo holeInfo) {
        if (mHoleArray.contains(holeInfo)) {
            mHoleArray.remove(holeInfo);
        }
        holeInfo.setCourseDbId(-1);
    }

    public DiscGolfHoleInfo findHoleByName(String name) {
        for (DiscGolfHoleInfo holeInfo : mHoleArray) {
            if (holeInfo.getName() == name) {
                return holeInfo;
            }
        }

        return null;
    }

    private DiscGolfHoleInfo findClosestHole(Location location, float threshold) {
        float bestDistance = threshold + 1;
        DiscGolfHoleInfo bestHole = null;
        for (DiscGolfHoleInfo holeInfo : mHoleArray) {
            Location teeLocation = holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.ADVANCED);
            if (teeLocation != null) {
                float distance = location.distanceTo(teeLocation);
                if ((distance <= threshold) && (distance < bestDistance)) {
                    bestHole = holeInfo;
                    bestDistance = distance;
                }
            }
        }

        return bestHole;
    }

    DiscGolfHoleInfo guessHole(DiscGolfDatabase database, Location location) {
        DiscGolfHoleInfo holeInfo = null;
        if (location == null) {
            // Return hole "1" or "A" if exists.
            holeInfo = findHoleByName("1");
            if (holeInfo != null) return holeInfo;

            // Return hole "10" if exists.
            holeInfo = findHoleByName("10");
            if (holeInfo != null) return holeInfo;
        } else {
            holeInfo = findClosestHole(location, 1000);
            if (holeInfo != null) return holeInfo;
        }

        // Else, return "any" that exists.
        if (mHoleArray.size() > 0) return mHoleArray.get(0);

        // If there are no holes yet associated with this course,
        // let's create one using default values.
        //TODO: Read settings here for defaults (e.g. par).
        holeInfo = new DiscGolfHoleInfo("1", 3);
        holeInfo.setCourseDbId(mDbId);
        holeInfo.setRoughLocation(location);
        mHoleArray.add(holeInfo);
        database.writeCourse(this);
        return holeInfo;
    }

    public DiscGolfHoleInfo findHoleByDbId(long holeInfoDbId) {
        for (DiscGolfHoleInfo holeInfo : mHoleArray) {
            if (holeInfo.getDbId() == holeInfoDbId) return holeInfo;
        }

        return null;
    }
}
