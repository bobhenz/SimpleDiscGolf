package com.bobhenz.simplediscgolf;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by bhenz on 9/9/2015.
 */
public class DiscGolfDbTableGameData {
    private static abstract class Table implements BaseColumns {
        public static final String TABLE_NAME = "game_data";
        public static final String COLUMN_NAME_START_TIME = "start_time";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_START_TIME + " INTEGER" +
                        " ) ";
        static final String SQL_DESTROY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static String whereId(long id) {
            return _ID + "=" + String.valueOf(id);
        }
    }

    DiscGolfDbTableGameData() {
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(Table.SQL_CREATE);
    }

    public void destroy(SQLiteDatabase db) {
        db.execSQL(Table.SQL_DESTROY);
    }

    public DiscGolfGameData read(SQLiteDatabase db, long dbId) {
        if (dbId < 0) return null;
        Cursor cursor = db.query(Table.TABLE_NAME, null, Table.whereId(dbId), null, null, null, null, null);
        if (cursor.getCount() == 0) return null;
        // If we found something, we better have only found 1 since course ID is supposed to be unique!
        assert (cursor.getCount() == 1);
        cursor.moveToFirst();
        DiscGolfGameData game = new DiscGolfGameData();
        game.setDbId(dbId);
        game.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(Table.COLUMN_NAME_START_TIME)));
        return game;
    }
}
