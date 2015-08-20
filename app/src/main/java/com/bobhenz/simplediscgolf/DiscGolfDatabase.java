package com.bobhenz.simplediscgolf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Currency;
import java.util.Map;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfDatabase {
    private static final int DATABASE_VERSION = 1;
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
            Log.d("Database", "Request to upgrade. TODO: implement");
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Database", "Request to downgrade. TODO: implement");
        }
    }

    DiscGolfDatabase(Context context) {
        mDatabase = new DbHelper(context);
        mTableCourseInfo = new DiscGolfDbTableCourseInfo();
        mTableHoleInfo = new DiscGolfDbTableHoleInfo();
    }

    DiscGolfCourseInfo createNewCourse() {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(mTableCourseInfo.calculateNewCourseName(db));
        writeCourse(courseInfo);
        return courseInfo;
    }

    public void writeCourse(DiscGolfCourseInfo courseInfo) {
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        mTableCourseInfo.write(db, courseInfo);
        // Write out the hole data for this course.
        for (DiscGolfHoleInfo holeInfo : courseInfo.getHoleList()) {
            mTableHoleInfo.write(db, holeInfo);
        }
    }

    private DiscGolfCourseInfo readCourse(SQLiteDatabase db, long courseDbId) {
        DiscGolfCourseInfo courseInfo = mTableCourseInfo.read(db, courseDbId);
        mTableHoleInfo.appendCourseHoles(db, courseInfo);
        return courseInfo;
    }

    public DiscGolfCourseInfo guessCourse(Location location) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        /**
         * Returns a course within a reasonable distance from
         * the current location. If location is unknown, or
         */
        if (location == null) {
            // TODO: Get the last course played and return that.
            // Else, return null.
            return null;
        }

        // TODO: Cycle through all the courses in the database and
        // look for a match within a "reasonable" radius of the
        // current location. (e.g. 5 miles?) Pick the closest
        // and return it. Else, return null.
        long courseDbId = mTableHoleInfo.findClosestCourse(db, location, 1000);
        if (courseDbId >= 0) {
            DiscGolfCourseInfo courseInfo = readCourse(db, courseDbId);
            return courseInfo;
        }

        return null;
    }

    public void debugPrintCourses() {
        Log.d("DB debug", "Course List");
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        mTableCourseInfo.printAll(db);
    }
}
