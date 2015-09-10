package com.bobhenz.simplediscgolf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;

/**
 * Created by bhenz on 9/9/2015.
 */
public class DiscGolfDbTableStrokeData {
    private static final double UNSET_LATITUDE = -360;
    private static final double UNSET_LONGITUDE = -360;
    private static final double UNSET_ALTITUDE = -1000000;

    private static abstract class Table implements BaseColumns {
        public static final String TABLE_NAME = "stroke_data";
        public static final String COLUMN_NAME_HOLE_DATA_ID = "hole_data_id";
        public static final String COLUMN_NAME_PREFIX_LOCATION = "location";
        public static final String COLUMN_NAME_POSTFIX_LATITUDE = "_latitude";
        public static final String COLUMN_NAME_POSTFIX_LONGITUDE = "_longitude";
        public static final String COLUMN_NAME_POSTFIX_ALTITUDE = "_altitude";
        public static final String COLUMN_NAME_IS_PENALTY = "is_penalty";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_HOLE_DATA_ID + " INTEGER" +
                        COLUMN_NAME_PREFIX_LOCATION + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_LOCATION + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_LOCATION + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE," +
                        COLUMN_NAME_IS_PENALTY + " BOOLEAN" +
                        " ) ";
        static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(Table.SQL_CREATE);
    }

    public void destroy(SQLiteDatabase db) {
        db.execSQL(Table.SQL_DESTROY);
    }

    private Location readLocationFromDbCursor(Cursor cursor, String prefix) {
        Location location = new Location("database");
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + Table.COLUMN_NAME_POSTFIX_LATITUDE));
        double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + Table.COLUMN_NAME_POSTFIX_LONGITUDE));
        if ((lat == UNSET_LATITUDE) || (lon == UNSET_LONGITUDE)) {
            return null;
        }
        location.setLatitude(lat);
        location.setLongitude(lon);

        double alt = cursor.getDouble(cursor.getColumnIndexOrThrow(prefix + Table.COLUMN_NAME_POSTFIX_ALTITUDE));
        if (alt != UNSET_ALTITUDE) {
            location.setAltitude(alt);
        }

        return location;
    }

    private DiscGolfStrokeData createStrokeDataFromDbCursor(Cursor cursor) {
        Location location = readLocationFromDbCursor(cursor, Table.COLUMN_NAME_PREFIX_LOCATION);
        boolean isPenalty = cursor.getInt(cursor.getColumnIndexOrThrow(Table.COLUMN_NAME_IS_PENALTY)) != 0;
        DiscGolfStrokeData stroke = new DiscGolfStrokeData(location, isPenalty);
        return stroke;
    }

    public void appendHoleStrokes(SQLiteDatabase db, DiscGolfHoleData hole) {
        Cursor cursor = db.query(Table.TABLE_NAME, null,
                Table.COLUMN_NAME_HOLE_DATA_ID + "=" + String.valueOf(hole.getDbId()),
                null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DiscGolfStrokeData stroke = createStrokeDataFromDbCursor(cursor);
            hole.addStroke(stroke);
        } // for

    }

}
