package com.example.m_hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "hike_db";
    private static final int DATABASE_VERSION = 3; // ⚠️ tăng version để cập nhật

    // Table Hikes + Columns
    public static final String TABLE_HIKES = "hikes";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_LOCATION = "location";
    public static final String COL_DATE = "date";
    public static final String COL_PARKING = "parking";
    public static final String COL_LENGTH = "length";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_WEATHER = "weather";
    public static final String COL_ELEVATION = "elevation";

    // Table Observations + Columns
    public static final String TABLE_OBSERVATIONS = "observations";
    public static final String COL_OBS_ID = "obs_id";
    public static final String COL_OBS_HIKE_ID = "hike_id";
    public static final String COL_OBS_TEXT = "observation";
    public static final String COL_OBS_TIME = "time";
    public static final String COL_OBS_COMMENT = "comment";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng hikes
        String createHikesTable = "CREATE TABLE " + TABLE_HIKES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_LOCATION + " TEXT NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_PARKING + " TEXT NOT NULL, " +
                COL_LENGTH + " REAL NOT NULL, " +
                COL_DIFFICULTY + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_WEATHER + " TEXT, " +
                COL_ELEVATION + " INTEGER" +
                ")";
        db.execSQL(createHikesTable);

        // Tạo bảng observations
        String createObsTable = "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                COL_OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_OBS_HIKE_ID + " INTEGER NOT NULL, " +
                COL_OBS_TEXT + " TEXT NOT NULL, " +
                COL_OBS_TIME + " TEXT NOT NULL, " +
                COL_OBS_COMMENT + " TEXT, " +
                "FOREIGN KEY(" + COL_OBS_HIKE_ID + ") REFERENCES " +
                TABLE_HIKES + "(" + COL_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(createObsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    // ================= CRUD CHO HIKES =================

    public boolean insertHike(String name, String location, String date, String parking,
                              double length, String difficulty, String description,
                              String weather, int elevation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_NAME, name);
        cv.put(COL_LOCATION, location);
        cv.put(COL_DATE, date);
        cv.put(COL_PARKING, parking);
        cv.put(COL_LENGTH, length);
        cv.put(COL_DIFFICULTY, difficulty);
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_WEATHER, weather);
        cv.put(COL_ELEVATION, elevation);

        long result = db.insert(TABLE_HIKES, null, cv);
        return result != -1;
    }

    public Cursor getAllHikes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKES + " ORDER BY " + COL_DATE + " DESC", null);
    }

    public Cursor getHikeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKES + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public boolean updateHike(int id, String name, String location, String date, String parking,
                              double length, String difficulty, String description,
                              String weather, int elevation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_NAME, name);
        cv.put(COL_LOCATION, location);
        cv.put(COL_DATE, date);
        cv.put(COL_PARKING, parking);
        cv.put(COL_LENGTH, length);
        cv.put(COL_DIFFICULTY, difficulty);
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_WEATHER, weather);
        cv.put(COL_ELEVATION, elevation);

        int result = db.update(TABLE_HIKES, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteHike(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_HIKES, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OBSERVATIONS, null, null);
        db.delete(TABLE_HIKES, null, null);
    }

    // ================= CRUD CHO OBSERVATIONS =================

    public boolean insertObservation(int hikeId, String observation, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_OBS_HIKE_ID, hikeId);
        cv.put(COL_OBS_TEXT, observation);
        cv.put(COL_OBS_TIME, time);
        cv.put(COL_OBS_COMMENT, comment);

        long result = db.insert(TABLE_OBSERVATIONS, null, cv);
        return result != -1;
    }

    public Cursor getObservationsByHike(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_OBSERVATIONS +
                        " WHERE " + COL_OBS_HIKE_ID + "=? ORDER BY " + COL_OBS_TIME + " DESC",
                new String[]{String.valueOf(hikeId)});
    }

    public boolean deleteObservation(int obsId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_OBSERVATIONS, COL_OBS_ID + "=?",
                new String[]{String.valueOf(obsId)});
        return result > 0;
    }

    public boolean updateObservation(int obsId, String observation, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_OBS_TEXT, observation);
        cv.put(COL_OBS_TIME, time);
        cv.put(COL_OBS_COMMENT, comment);

        int result = db.update(TABLE_OBSERVATIONS, cv, COL_OBS_ID + "=?",
                new String[]{String.valueOf(obsId)});
        return result > 0;
    }

    public int getObservationCount(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_OBSERVATIONS +
                        " WHERE " + COL_OBS_HIKE_ID + "=?",
                new String[]{String.valueOf(hikeId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ================= SEARCH FUNCTIONALITY =================
    public Cursor searchHikes(String name, String location, String length, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_HIKES + " WHERE 1=1";
        ArrayList<String> args = new ArrayList<>();

        if (!name.isEmpty()) {
            query += " AND " + COL_NAME + " LIKE ?";
            args.add("%" + name + "%");
        }
        if (!location.isEmpty()) {
            query += " AND " + COL_LOCATION + " LIKE ?";
            args.add("%" + location + "%");
        }
        if (!length.isEmpty()) {
            query += " AND " + COL_LENGTH + " = ?";
            args.add(length);
        }
        if (!date.isEmpty()) {
            query += " AND " + COL_DATE + " LIKE ?";
            args.add("%" + date + "%");
        }

        return db.rawQuery(query, args.toArray(new String[0]));
    }
}
