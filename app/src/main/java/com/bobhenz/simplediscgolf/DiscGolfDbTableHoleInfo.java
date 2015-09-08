package com.bobhenz.simplediscgolf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DiscGolfDbTableHoleInfo {
    private static final double UNSET_LATITUDE = -360;
    private static final double UNSET_LONGITUDE = -360;
    private static final double UNSET_ALTITUDE = -1000000;

    public static abstract class HoleTable implements BaseColumns {
        public static final String TABLE_NAME = "hole_info";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_COURSE_ID = "course";
        public static final String COLUMN_NAME_PAR = "par";
        public static final String COLUMN_NAME_PREFIX_TEE_NOVICE = "tee_novice";
        public static final String COLUMN_NAME_PREFIX_TEE_INTERMEDIATE = "tee_intermediate";
        public static final String COLUMN_NAME_PREFIX_TEE_ADVANCED = "tee_advanced";
        public static final String COLUMN_NAME_POSTFIX_LATITUDE = "_latitude";
        public static final String COLUMN_NAME_POSTFIX_LONGITUDE = "_longitude";
        public static final String COLUMN_NAME_POSTFIX_ALTITUDE = "_altitude";
        public static final String COLUMN_NAME_IS_ROUGH_LOCATION = "is_rough_location";
        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NUMBER + " TEXT," +
                        COLUMN_NAME_COURSE_ID + " INTEGER," +
                        COLUMN_NAME_PAR + " PAR," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_ADVANCED + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_INTERMEDIATE + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_INTERMEDIATE + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_INTERMEDIATE + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_NOVICE + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_NOVICE + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_TEE_NOVICE + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE," +
                        COLUMN_NAME_IS_ROUGH_LOCATION + " BOOLEAN" +
                        " ) ";
        public static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(HoleTable.SQL_CREATE);
        // Create a bunch of "null" holes that the "null course" can utilize.
        for (int holeNumber = 1; holeNumber <= 99; holeNumber++) {
            DiscGolfHoleInfo newHole = new DiscGolfHoleInfo(String.format("%d", holeNumber), 3);
            newHole.setCourseDbId(DiscGolfCourseInfo.NULL_COURSE_DB_ID);
            write(db, newHole);
        }
    }

    public void destroy(SQLiteDatabase db) {
        db.execSQL(HoleTable.SQL_DESTROY);
    }

    private Location readLocationFromDbCursor(Cursor cursor, String prefix) {
        Location location = new Location("database");
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + HoleTable.COLUMN_NAME_POSTFIX_LATITUDE));
        double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + HoleTable.COLUMN_NAME_POSTFIX_LONGITUDE));
        if ((lat == UNSET_LATITUDE) || (lon == UNSET_LONGITUDE)) {
            return null;
        }
        location.setLatitude(lat);
        location.setLongitude(lon);

        double alt = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + HoleTable.COLUMN_NAME_POSTFIX_ALTITUDE));
        if (alt != UNSET_ALTITUDE) {
            location.setAltitude(alt);
        }

        return location;
    }

    private DiscGolfHoleInfo createHoleInfoFromDbCursor(Cursor cursor) {
        DiscGolfHoleInfo holeInfo = new DiscGolfHoleInfo("", 3);
        holeInfo.setDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable._ID)));
        holeInfo.setCourseDbId(cursor.getLong(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_COURSE_ID)));
        holeInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_NUMBER)));
        holeInfo.setPar(cursor.getInt(cursor.getColumnIndexOrThrow(HoleTable.COLUMN_NAME_PAR)));

        Location location;
        location = readLocationFromDbCursor(cursor, HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED);
        holeInfo.setTeeLocation(DiscGolfHoleInfo.TeeCategory.ADVANCED, location);
        location = readLocationFromDbCursor(cursor, HoleTable.COLUMN_NAME_PREFIX_TEE_INTERMEDIATE);
        holeInfo.setTeeLocation(DiscGolfHoleInfo.TeeCategory.INTERMEDIATE, location);
        location = readLocationFromDbCursor(cursor, HoleTable.COLUMN_NAME_PREFIX_TEE_NOVICE);
        holeInfo.setTeeLocation(DiscGolfHoleInfo.TeeCategory.NOVICE, location);

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

    public DiscGolfHoleInfo read(SQLiteDatabase db, long dbId) {
        Cursor cursor = db.query(HoleTable.TABLE_NAME, null, HoleTable.whereId(dbId),
                null, null, null, null, null);
        cursor.moveToFirst();
        assert(cursor.getCount() == 1);
        return createHoleInfoFromDbCursor(cursor);
    }

    public void write(SQLiteDatabase db, DiscGolfHoleInfo holeInfo) {
        ContentValues hole_values = new ContentValues();
        hole_values.put(HoleTable.COLUMN_NAME_COURSE_ID, holeInfo.getCourseDbId());
        hole_values.put(HoleTable.COLUMN_NAME_NUMBER, holeInfo.getName());
        hole_values.put(HoleTable.COLUMN_NAME_PAR, holeInfo.getPar());
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_NOVICE, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.NOVICE));
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_INTERMEDIATE, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.INTERMEDIATE));
        appendLocationValues(hole_values, HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED, holeInfo.getTeeLocation(DiscGolfHoleInfo.TeeCategory.ADVANCED));
        hole_values.put(HoleTable.COLUMN_NAME_IS_ROUGH_LOCATION, holeInfo.getIsRoughLocation());
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
        ArrayList<Long> list = findClosestCourses(db, location, thresholdMeters, 1);
        if (list.size() < 1) return -1;
        return list.get(0);
    }

    public long readCourseIdFromHoleId(SQLiteDatabase db, long holeId) {
        /* Return: Course database ID if found, else -1 if not found. */
        String[] columns = {
                HoleTable.COLUMN_NAME_COURSE_ID
        };
        Cursor cursor = db.query(HoleTable.TABLE_NAME, columns, HoleTable.whereId(holeId), null, null, null, null, null);
        if (cursor.getCount() == 0) return -1;
        // If we found something, we better have only found 1 since course ID is supposed to be unique!
        assert (cursor.getCount() == 1);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }

    private ArrayList<Long> sortByDistance(Map<Long, Float> map) {
        ArrayList<Map.Entry<Long, Float>> list = new ArrayList<Map.Entry<Long, Float>>(map.entrySet());
        Collections.sort(list,
                new Comparator<Map.Entry<Long, Float>>() {
                    public int compare(Map.Entry<Long, Float> r1, Map.Entry<Long, Float> r2) {
                        return r1.getValue().compareTo(r2.getValue());
                    }
                });
        ArrayList<Long> idList = new ArrayList<>();
        for (Map.Entry<Long, Float> entry : list) {
            idList.add(entry.getKey());
        }

        return idList;
    }

    public ArrayList<Long> findClosestCourses(SQLiteDatabase db, Location location, float thresholdMeters, int sizeLimit) {
        // Read all holes within the requested vicinity into a map.
        String[] columns = {
                HoleTable.COLUMN_NAME_COURSE_ID,
                HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED + HoleTable.COLUMN_NAME_POSTFIX_LATITUDE,
                HoleTable.COLUMN_NAME_PREFIX_TEE_ADVANCED + HoleTable.COLUMN_NAME_POSTFIX_LONGITUDE
        };
        Cursor cursor = db.query(HoleTable.TABLE_NAME, columns, null, null, null, null, null, null);
        Map<Long, Float> courseMap = new HashMap<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            double lat = cursor.getDouble(1);
            double lon = cursor.getDouble(2);
            Location holeLocation = new Location("database");
            holeLocation.setLatitude(lat);
            holeLocation.setLongitude(lon);
            Float distance = holeLocation.distanceTo(location);
            Long id = cursor.getLong(0);
            if (distance <= thresholdMeters) {
                // Keep the closest so that when we sort by distance,
                // we return the closest course. Probably not an important
                // issue, but might as well try our best to do what we say
                // we're going to do.
                if (!courseMap.containsKey(id) || (courseMap.get(id) > distance)) {
                    courseMap.put(id, distance);
                }
            }
        } // for

        // Sort the entries by distance.
        ArrayList<Long> courseList = sortByDistance(courseMap);

        // Remove the ones that are too far away.
        if (courseList.size() > sizeLimit) {
            courseList = new ArrayList<>(courseList.subList(0, sizeLimit));
        }

        return courseList;
    }

    public void printAll(SQLiteDatabase db) {
        String[] columns = {HoleTable.COLUMN_NAME_NUMBER, HoleTable._ID, HoleTable.COLUMN_NAME_COURSE_ID, };
        Cursor cursor = db.query(HoleTable.TABLE_NAME, columns, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("Hole", String.format("\"%s\",%d (course:%d)", cursor.getString(0), cursor.getLong(1), cursor.getLong(2)));
        }
    }}
