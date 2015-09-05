package com.bobhenz.simplediscgolf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import java.util.ArrayList;

public class DiscGolfDatabase {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "DiscGolfCourses.db";

    private DbHelper mDatabase;
    private DiscGolfDbTableCourseInfo mTableCourseInfo;
    private DiscGolfDbTableHoleInfo mTableHoleInfo;

    private class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            mTableCourseInfo.create(db);
            mTableHoleInfo.create(db);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mTableCourseInfo.destroy(db);
            mTableHoleInfo.destroy(db);
            mTableCourseInfo.create(db);
            mTableHoleInfo.create(db);

        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    DiscGolfDatabase(Context context) {
        mDatabase = new DbHelper(context);
        mTableCourseInfo = new DiscGolfDbTableCourseInfo();
        mTableHoleInfo = new DiscGolfDbTableHoleInfo();
    }

    DiscGolfCourseInfo createNewCourse(Location location) {
        // Create a new course
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(mTableCourseInfo.calculateNewCourseName(db));

        // and give it a hole so that we can store the provided location.
        DiscGolfHoleInfo holeInfo = new DiscGolfHoleInfo("1", 3);
        holeInfo.setRoughLocation(location);
        courseInfo.addHole(holeInfo);

        // Finally, write it into the database so that the next time
        // the user is at this locaiton, we can use the same course.
        writeCourse(courseInfo);
        return courseInfo;
    }

    public void writeCourse(DiscGolfCourseInfo courseInfo) {
        // Do NOT write the null course into the database.
        if (!courseInfo.getIsNull()) {
            SQLiteDatabase db = mDatabase.getWritableDatabase();
            mTableCourseInfo.write(db, courseInfo);
            // Write out the hole data for this course.
            for (DiscGolfHoleInfo holeInfo : courseInfo.getHoleList()) {
                mTableHoleInfo.write(db, holeInfo);
            } // for
        }
    }

    private DiscGolfCourseInfo readCourse(SQLiteDatabase db, long courseDbId) {
        DiscGolfCourseInfo courseInfo = mTableCourseInfo.read(db, courseDbId);
        if (courseInfo != null) {
            mTableHoleInfo.appendCourseHoles(db, courseInfo);
        }
        return courseInfo;
    }

    public DiscGolfCourseInfo readCourse(long courseDbId) {
        return readCourse(mDatabase.getReadableDatabase(), courseDbId);
    }

    public DiscGolfCourseInfo readCourseFromHoleInfoDbId(long holeInfoDbId) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        long courseDbId = mTableHoleInfo.readCourseIdFromHoleId(db, holeInfoDbId);
        return readCourse(db, courseDbId);
    }

    public DiscGolfHoleInfo readHoleInfo(long holeInfoDbId) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        return mTableHoleInfo.read(db, holeInfoDbId);
    }

    private DiscGolfCourseInfo createNullCourse(SQLiteDatabase db) {
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(DiscGolfCourseInfo.NULL_COURSE_NAME);
        courseInfo.setDbId(DiscGolfCourseInfo.NULL_COURSE_DB_ID);
        courseInfo.setIsNull(true);
        mTableHoleInfo.appendCourseHoles(db, courseInfo);
        return courseInfo;
    }

    public DiscGolfCourseInfo guessCourse(Location location) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        long courseDbId = -1;

        if (location != null) {
            // If we have a location, see if we can find a course near there.
            courseDbId = mTableHoleInfo.findClosestCourse(db, location, 1000);
            if (courseDbId >= 0) {
                return readCourse(db, courseDbId);
            } else {
                return createNewCourse(location);
            }
        }

        // If we did not have a location,
        // look for one recently played.
        //TODO
        //courseDbId = mTableHoleData.findMostRecentCourse(db);
        //if (courseDbId >= 0) {
        //    return readCourse(db, courseDbId);
        //}

        // If we still don't have one, create the special null course.
        return createNullCourse(db);
    }

    public ArrayList<Long> findCoursesNear(Location location, int radiusMeters, int maximumCount) {
        /**
         * Returns a list of the closest courses within a certain distance from
         * the given location.
         */
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        if (location == null) {
            return null;
        }

        return mTableHoleInfo.findClosestCourses(db, location, radiusMeters, maximumCount);
    }

    public ArrayList<String> getCourseNames(ArrayList<Long> dbIdList) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        ArrayList<String> nameList = new ArrayList<>();
        for (long dbId : dbIdList) {
            if (dbId < 0) {
                // It's possible that the NULL course was added
                // to this list (dbId = -1) so handle that case.
                nameList.add(DiscGolfCourseInfo.NULL_COURSE_NAME);
            } else {
                // Otherwise get the name of the course.
                nameList.add(mTableCourseInfo.read(db, dbId).getName());
            }
        } // for
        return nameList;
    }

    public ArrayList<Long> findRecentCourses(int maximumCount) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        // this is totally bogus. Should not be a method of mTableCourseInfo.
        // needs to be a method on mTableGameInfo.
        ArrayList<Long> list = mTableCourseInfo.readRecent(db, maximumCount);
        return list;
    }

    public void debugPrintCourses() {
        Log.d("DB debug", "Course List");
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        mTableCourseInfo.printAll(db);
    }
}
