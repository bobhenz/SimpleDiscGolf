package com.bobhenz.simplediscgolf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;


public class DiscGolfDbTableHoleData {
    private static final double UNSET_LATITUDE = -360;
    private static final double UNSET_LONGITUDE = -360;
    private static final double UNSET_ALTITUDE = -1000000;

    private static abstract class Table implements BaseColumns {
        public static final String TABLE_NAME = "hole_data";
        public static final String COLUMN_NAME_GAME_ID = "game_id";
        public static final String COLUMN_NAME_HOLE_INFO_ID = "hole_info_id";
        public static final String COLUMN_NAME_PREFIX_START_LOCATION = "location";
        public static final String COLUMN_NAME_POSTFIX_LATITUDE = "_latitude";
        public static final String COLUMN_NAME_POSTFIX_LONGITUDE = "_longitude";
        public static final String COLUMN_NAME_POSTFIX_ALTITUDE = "_altitude";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_GAME_ID + " INTEGER," +
                        COLUMN_NAME_HOLE_INFO_ID + " INTEGER" +
                        COLUMN_NAME_PREFIX_START_LOCATION + COLUMN_NAME_POSTFIX_LATITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_START_LOCATION + COLUMN_NAME_POSTFIX_LONGITUDE + " DOUBLE," +
                        COLUMN_NAME_PREFIX_START_LOCATION + COLUMN_NAME_POSTFIX_ALTITUDE + " DOUBLE" +
                        " ) ";
        static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    DiscGolfDbTableHoleData() {
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

    private DiscGolfHoleData createHoleDataFromDbCursor(Cursor cursor) {
        DiscGolfHoleData hole = new DiscGolfHoleData(cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_NAME_HOLE_INFO_ID)));
        hole.setStartLocation(readLocationFromDbCursor(cursor, Table.COLUMN_NAME_PREFIX_START_LOCATION));
        return hole;
    }

    public void appendGameHoles(SQLiteDatabase db, DiscGolfGameData game) {
        Cursor cursor = db.query(Table.TABLE_NAME, null,
                Table.COLUMN_NAME_GAME_ID + "=" + String.valueOf(game.getDbId()),
                null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DiscGolfHoleData hole = createHoleDataFromDbCursor(cursor);
            game.addHole(hole);
        } // for

    }
}
