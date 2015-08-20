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
    private static final double UNSET_LATITUDE = -360;
    private static final double UNSET_LONGITUDE = -360;
    private static final double UNSET_ALTITUDE = -1000000;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DiscGolfCourses.db";

    private DbHelper mDatabase;

    public static abstract class CourseTable implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_NAME_NAME = "name";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT" +
                        " ) ";
        static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    public static abstract class HoleTable implements BaseColumns {
        public static final String TABLE_NAME = "hole_info";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_COURSE_ID = "course";
        public static final String COLUMN_NAME_PREFIX_TEE_NOVICE = "tee_novice";
        public static final String COLUMN_NAME_PREFIX_TEE_INTERMEDIATE = "tee_intermediate";
        public static final String COLUMN_NAME_PREFIX_TEE_ADVANCED = "tee_advanced";
        public static final String COLUMN_NAME_POSTFIX_LATITUDE = "_latitude";
        public static final String COLUMN_NAME_POSTFIX_LONGITUDE = "_longitude";
        public static final String COLUMN_NAME_POSTFIX_ALTITUDE = "_altitude";
        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NUMBER + " TEXT," +
                        COLUMN_NAME_COURSE_ID + " INTEGER," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE" +
                        " ) ";
        public static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    private class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CourseTable.SQL_CREATE);
            db.execSQL(HoleTable.SQL_CREATE);
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
    }

    private long getMaximumId(String table)
    {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + BaseColumns._ID + ") FROM " + table, null);
        cursor.moveToFirst();
        long maxId = cursor.getLong(0);
        cursor.close();
        return maxId;
    }

    private String calculateNewCourseName() {
        return String.format("Course #%d", getMaximumId(CourseTable.TABLE_NAME) + 1);
    }

    DiscGolfCourseInfo createNewCourse() {
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(calculateNewCourseName());
        writeCourse(courseInfo);
        return courseInfo;
    }

    private void appendLocationValues(ContentValues values, String columnNamePrefix, Location location) {
        double lat = UNSET_LATITUDE;
        double lon = UNSET_LONGITUDE;
        double alt = UNSET_ALTITUDE;
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLatitude();
            alt = location.getAltitude();
        }
        values.put(columnNamePrefix + HoleTable.COLUMN_NAME_POSTFIX_LATITUDE, lat);
        values.put(columnNamePrefix + HoleTable.COLUMN_NAME_POSTFIX_LONGITUDE, lon);
        values.put(columnNamePrefix + HoleTable.COLUMN_NAME_POSTFIX_ALTITUDE, alt);
    }

    private void writeHoleInfo (SQLiteDatabase db, long courseDbId, DiscGolfHoleInfo holeInfo) {
        ContentValues hole_values = new ContentValues();
        hole_values.put(HoleTable.COLUMN_NAME_COURSE_ID, courseDbId);
        hole_values.put(HoleTable.COLUMN_NAME_NUMBER, holeInfo.getName());
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_NOVICE, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.NOVICE));
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_INTERMEDIATE, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.INTERMEDIATE));
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.ADVANCED));
        if (holeInfo.getDbId() < 0) {
            long newId = db.insert(HoleTable.TABLE_NAME, null, hole_values);
            holeInfo.setDbId(newId);
        } else {
            long count = db.update(HoleTable.TABLE_NAME, hole_values, HoleTable.whereId(holeInfo.getDbId()), null);
            if (count == 0) {
                throw new RuntimeException("Database entry expected but not found");
            }
        }
    }

    public void writeCourse(DiscGolfCourseInfo courseInfo) {
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CourseTable.COLUMN_NAME_NAME, courseInfo.getName());
        if (courseInfo.getDbId() < 0) {
            // New course, append it to the course table.
            long newId = db.insert(CourseTable.TABLE_NAME, null, values);
            // Update the course with the new ID from the database entry.
            courseInfo.setDbId(newId);
        } else {
            // Existing course, find the entry and update it.
            long count = db.update(CourseTable.TABLE_NAME, values, CourseTable.whereId(courseInfo.getDbId()), null);
            if (count == 0) {
                throw new RuntimeException("Database entry expected but not found");
            }
        }

        // Write out the hole data for this course.
        for (DiscGolfHoleInfo holeInfo : courseInfo.getHoleList()) {
            writeHoleInfo(db, courseInfo.getDbId(), holeInfo);
        }
    }

    private long findClosestCourse(SQLiteDatabase db, Location location, float thresholdMeters) {
        /* Return: Course database ID if found, else -1 if not found. */
        String[] columns = {
                HoleTable.COLUMN_NAME_COURSE_ID,
                HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED + HoleTable.COLUMN_NAME_POSTFIX_LATITUDE,
                HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED + HoleTable.COLUMN_NAME_POSTFIX_LONGITUDE
        };
        Cursor cursor = db.query(HoleTable.TABLE_NAME, columns, null, null, null, null, null, null);
        long bestCourseDbId = -1;
        float bestCourseDistance = thresholdMeters + 1;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            double lat = cursor.getDouble(1);
            double lon = cursor.getDouble(2);
            Location holeLocation = new Location("database");
            holeLocation.setLatitude(lat);
            holeLocation.setLongitude(lon);
            float distance = holeLocation.distanceTo(location);
            if ((distance <= thresholdMeters) && (distance < bestCourseDistance)) {
                bestCourseDbId = cursor.getLong(0);
            }
        } // for

        return bestCourseDbId;
    }

    private DiscGolfHoleInfo createHoleInfoFromDbCursor(Cursor cursor) {
        DiscGolfHoleInfo holeInfo = new DiscGolfHoleInfo("", 3);
        holeInfo.setDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable._ID)));
        holeInfo.setCourseDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_COURSE_ID)));
        holeInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_NUMBER)));

        return holeInfo;
    }

    private void readCourseHoles(SQLiteDatabase db, DiscGolfCourseInfo courseInfo) {
        Cursor cursor = db.query(HoleTable.TABLE_NAME, null,
                HoleTable.COLUMN_NAME_COURSE_ID + "=" + String.valueOf(courseInfo.getDbId()),
                null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DiscGolfHoleInfo holeInfo = createHoleInfoFromDbCursor(cursor);
            courseInfo.addHole(holeInfo);
        } // for
    }

    private DiscGolfCourseInfo readCourse(SQLiteDatabase db, long courseDbId) {
        if (courseDbId < 0) return null;
        String[] columns = {CourseTable.COLUMN_NAME_NAME};
        Cursor cursor = db.query(CourseTable.TABLE_NAME, columns, CourseTable.whereId(courseDbId), null, null, null, null, null);
        if (cursor.getCount() == 0) return null;
        // If we found something, we better have only found 1 since course ID is supposed to be unique!
        assert (cursor.getCount() == 1);
        cursor.moveToFirst();
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(cursor.getString(0));
        courseInfo.setDbId(courseDbId);
        readCourseHoles(db, courseInfo);
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
        long courseDbId = findClosestCourse(db, location, 1000);
        if (courseDbId >= 0) {
            DiscGolfCourseInfo courseInfo = readCourse(db, courseDbId);
            return courseInfo;
        }

        return null;
    }

    public void refresh() {
        // TODO: Reload data from storage.
    }

    public void flush() {
        //TODO: Flush (merge?) data to storage.
    }

    public void debugPrintCourses() {
        Log.d("DB debug", "Course List");
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        String[] columns = {CourseTable.COLUMN_NAME_NAME};
        Cursor cursor = db.query(CourseTable.TABLE_NAME, columns, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("Name:", cursor.getString(0));
        }
    }
}
