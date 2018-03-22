package com.boscojbtechventures.noter.Repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.boscojbtechventures.noter.DbService.DbAdapter;
import com.boscojbtechventures.noter.Model.Note;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Johnbosco on 01/07/2017.
 */

public class NoteRepository extends DbAdapter {

    private static final String TABLE_NAME = "notes";
    private static final String KEY_ID = "_id";
    private static final String KEY_NOTE_NAME = "name";
    private static final String KEY_NOTE_DATE = "date";
    private static final String KEY_NOTE_PATH = "path";
    private static final String KEY_NOTE_ABS_PATH = "abs_path";
    private static final String KEY_DRAWING = "drawing";
    private static final String KEY_DRAWING_PATH = "drawing_path";
    private static final String KEY_CREATED_DATE = "create_date";
    private static final String KEY_UPDATE_DATE = "update_date";
    private static final String KEY_DELETED = "delete_flag";

    public NoteRepository(Context context) {
        super(context);
    }

    public long addNote(Note note) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        long created;

        values.put(KEY_NOTE_NAME, note.getName());
        values.put(KEY_NOTE_DATE, note.getDate().toString());
        values.put(KEY_NOTE_PATH, note.getPath());
        values.put(KEY_NOTE_ABS_PATH, note.getAbsPath());
        values.put(KEY_DRAWING, note.getHasDrawing());
        values.put(KEY_DRAWING_PATH, note.getDrawingPath());
        values.put(KEY_CREATED_DATE, new Date().toString());
        values.put(KEY_DELETED, note.getDeleted());

        created = db.insert(TABLE_NAME, null, values);
        db.close();
        values.clear();
        return created;
    }

    public Note viewNote(int id) {
        SQLiteDatabase db = OpenDb();
        Note note = new Note();
        Cursor cursor =db.query(TABLE_NAME, new String[]{
                        KEY_NOTE_NAME, KEY_NOTE_DATE, KEY_NOTE_PATH, KEY_NOTE_ABS_PATH, KEY_DELETED},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            note.setName(cursor.getString(0));
            note.setDate(cursor.getString(1));
            note.setPath(cursor.getString(2));
            note.setAbsPath(cursor.getString(3));
            note.setDeleted(cursor.getInt(4));
            cursor.close();
        }
        db.close();
        return note;
    }

    public ArrayList<Note> getAllNotes() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_DELETED + " = 0 ORDER BY " + KEY_ID + " DESC LIMIT 5", null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setName(cursor.getString(1));
                //note.setDate(new Date(cursor.getString(2)));
                note.setPath(cursor.getString(3));
                note.setAbsPath(cursor.getString(4));
                note.setDeleted(cursor.getInt(5));
                notes.add(note);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notes;
    }

    public long deleteNote(Note note) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        long created;

        values.put(KEY_DELETED, 1);
        values.put(KEY_UPDATE_DATE, new Date().toString());

        created = db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(note.getId())});
        db.close();
        values.clear();
        return created;
    }

    public long undoDeleteNote(Note note) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        long created;

        values.put(KEY_DELETED, 0);
        values.put(KEY_UPDATE_DATE, new Date().toString());

        created = db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(note.getId())});
        db.close();
        values.clear();
        return created;
    }
}
