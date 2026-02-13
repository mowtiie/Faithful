package com.mowtiie.faithful.data.thought;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.mowtiie.faithful.data.Database;

import java.util.ArrayList;

public class ThoughtRepository extends Database {

    public ThoughtRepository(@Nullable Context context) {
        super(context);
    }

    public void add(Thought thought) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_THOUGHT_ID, thought.getId());
        values.put(COLUMN_THOUGHT_CONTENT, thought.getContent());
        values.put(COLUMN_THOUGHT_TIMESTAMP, thought.getTimestamp());
        sqLiteDatabase.insert(TABLE_THOUGHT, null, values);
        sqLiteDatabase.close();
    }

    public void delete(String thoughtId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_THOUGHT, COLUMN_THOUGHT_ID + " = ?", new String[]{thoughtId});
        sqLiteDatabase.close();
    }

    public ArrayList<Thought> getAll() {
        ArrayList<Thought> thoughts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_THOUGHT, null);

        if (cursor.moveToFirst()) {
            do {
                Thought thought = new Thought();
                thought.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THOUGHT_ID)));
                thought.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THOUGHT_CONTENT)));
                thought.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THOUGHT_TIMESTAMP)));
                thoughts.add(thought);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return thoughts;
    }
}