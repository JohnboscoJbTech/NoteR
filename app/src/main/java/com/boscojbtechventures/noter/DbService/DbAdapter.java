package com.boscojbtechventures.noter.DbService;

/**
 * Created by Johnbosco on 13/02/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

    private static MyDatabaseManager mDbManager;
    private Context mContext;

    //NOTE: date can be save as either text(string format), int(Unix time), real(julian day numbers)
    private static final String CREATE_NOTE_TABLE = "CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, date TEXT, " +
            "path TEXT, abs_path TEXT, drawing INTEGER, drawing_path TEXT, create_date DATE, update_date DATE, delete_flag INTEGER)";
    private static final String DROP_NOTE_TABLE = "DROP TABLE IF EXISTS notes";

    public DbAdapter(Context context) {
        mContext = context.getApplicationContext();
    }

    protected SQLiteDatabase OpenDb() {
        if (mDbManager == null) {
            mDbManager = new MyDatabaseManager(mContext);
        }
        return mDbManager.getWritableDatabase();
    }

    public void CloseDb() {
        mDbManager.close();
    }

    private static class MyDatabaseManager extends SQLiteOpenHelper {
        private static String DATABASE_NAME = "noter";
        private static int DATABASE_VERSION = 1;

        private MyDatabaseManager(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NOTE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL(DROP_NOTE_TABLE);
            this.onCreate(db);
        }
    }
}
