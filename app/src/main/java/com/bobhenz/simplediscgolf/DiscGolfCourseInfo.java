package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfCourseInfo {
    private List<DiscGolfHoleInfo> mHoleArray = new ArrayList<DiscGolfHoleInfo>();

    public DiscGolfCourseInfo() {
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
        DiscGolfHoleInfo hole = new DiscGolfHoleInfo("1", 3, DiscGolfHoleInfo.TeeCategory.UNKNOWN);
        if (location != null) {
            hole.setTeeLocation(location);
        }
        mHoleArray.add(hole);
        return hole;
    }
}
