package com.bobhenz.simplediscgolf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class DiscGolfDbTableCourseInfo {
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

    DiscGolfDbTableCourseInfo() {
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(CourseTable.SQL_CREATE);
    }

    private long getMaximumId(SQLiteDatabase db, String table)
    {
        Cursor cursor = db.rawQuery("SELECT MAX(" + BaseColumns._ID + ") FROM " + table, null);
        cursor.moveToFirst();
        long maxId = cursor.getLong(0);
        cursor.close();
        return maxId;
    }

    public String calculateNewCourseName(SQLiteDatabase db) {
        return String.format("Course #%d", getMaximumId(db, CourseTable.TABLE_NAME) + 1);
    }

    public DiscGolfCourseInfo read(SQLiteDatabase db, long courseDbId) {
        if (courseDbId < 0) return null;
        String[] columns = {CourseTable.COLUMN_NAME_NAME};
        Cursor cursor = db.query(CourseTable.TABLE_NAME, columns, CourseTable.whereId(courseDbId), null, null, null, null, null);
        if (cursor.getCount() == 0) return null;
        // If we found something, we better have only found 1 since course ID is supposed to be unique!
        assert (cursor.getCount() == 1);
        cursor.moveToFirst();
        DiscGolfCourseInfo courseInfo = new DiscGolfCourseInfo(cursor.getString(0));
        courseInfo.setDbId(courseDbId);
        return courseInfo;
    }

    public void write(SQLiteDatabase db, DiscGolfCourseInfo courseInfo) {
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
    }

    public void printAll(SQLiteDatabase db) {
        String[] columns = {CourseTable.COLUMN_NAME_NAME};
        Cursor cursor = db.query(CourseTable.TABLE_NAME, columns, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("Name:", cursor.getString(0));
        }
    }
}
