package com.bobhenz.simplediscgolf;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfGameData {
    private DiscGolfCourseInfo mCourse;
    private List<DiscGolfHoleData> holeArray = new ArrayList<DiscGolfHoleData>();
    private DiscGolfHoleData mCurrentHole;

    DiscGolfGameData(DiscGolfDatabase database, Location location) {
        mCourse = database.guessCourse(location);
        if (mCourse == null) {
            mCourse = database.createNewCourse();
        }

        database.debugPrintCourses();
        database.createNewCourse();
        database.debugPrintCourses();
    }

    public DiscGolfCourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(DiscGolfCourseInfo course) {
        this.mCourse = course;
    }

    public void addHole(DiscGolfHoleInfo holeInfo) {
        DiscGolfHoleData hole = new DiscGolfHoleData(holeInfo);
        holeArray.add(hole);
        mCurrentHole = hole;
    }

    public DiscGolfHoleData getHole() {
        return mCurrentHole;
    }
}
