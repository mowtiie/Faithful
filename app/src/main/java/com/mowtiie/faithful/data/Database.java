package com.mowtiie.faithful.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "faithful.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_THOUGHT = "thoughts";
    public static final String COLUMN_THOUGHT_ID = "id";
    public static final String COLUMN_THOUGHT_CONTENT = "content";
    public static final String COLUMN_THOUGHT_TIMESTAMP = "timestamp";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createThoughtTable = "CREATE TABLE IF NOT EXISTS " + TABLE_THOUGHT + "(" +
                COLUMN_THOUGHT_ID + " TEXT PRIMARY KEY, " +
                COLUMN_THOUGHT_CONTENT + " TEXT NOT NULL, " +
                COLUMN_THOUGHT_TIMESTAMP + " INTEGER NOT NULL);";
        db.execSQL(createThoughtTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THOUGHT);
    }
}
