package edu.sjsu.cs.davsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DAVSyncOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "davsync_db";

    public DAVSyncOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE credentials ( key TEXT PRIMARY KEY, val TEXT );");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /*
        INSERT INTO credentials VALUES('username','michael');
        SELECT val FROM credentials WHERE key = 'username';
    */
}
