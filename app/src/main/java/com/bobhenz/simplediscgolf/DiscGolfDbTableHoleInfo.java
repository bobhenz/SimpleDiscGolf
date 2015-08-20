package com.bobhenz.simplediscgolf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;

public class DiscGolfDbTableHoleInfo {
    private static final double UNSET_LATITUDE = -360;
    private static final double UNSET_LONGITUDE = -360;
    private static final double UNSET_ALTITUDE = -1000000;

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

    public void create(SQLiteDatabase db) {
        db.execSQL(HoleTable.SQL_CREATE);
    }

    private DiscGolfHoleInfo createHoleInfoFromDbCursor(Cursor cursor) {
        DiscGolfHoleInfo holeInfo = new DiscGolfHoleInfo("", 3);
        holeInfo.setDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable._ID)));
        holeInfo.setCourseDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_COURSE_ID)));
        holeInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_NUMBER)));
        return holeInfo;
    }

    public void appendCourseHoles(SQLiteDatabase db, DiscGolfCourseInfo courseInfo) {
        Cursor cursor = db.query(HoleTable.TABLE_NAME, null,
                HoleTable.COLUMN_NAME_COURSE_ID + "=" + String.valueOf(courseInfo.getDbId()),
                null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DiscGolfHoleInfo holeInfo = createHoleInfoFromDbCursor(cursor);
            courseInfo.addHole(holeInfo);
        } // for
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

    public void write(SQLiteDatabase db, DiscGolfHoleInfo holeInfo) {
        ContentValues hole_values = new ContentValues();
        hole_values.put(HoleTable.COLUMN_NAME_COURSE_ID, holeInfo.getCourseDbId());
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

    public long findClosestCourse(SQLiteDatabase db, Location location, float thresholdMeters) {
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

}
