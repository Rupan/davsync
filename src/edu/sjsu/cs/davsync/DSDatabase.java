package edu.sjsu.cs.davsync;

import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DSDatabase {

	// davcync open helper - handles database initialization
	private class DSOH extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 2;
		private static final String DATABASE_NAME = "davsync_db";

		public DSOH(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE dav_profiles ( hostname TEXT PRIMARY KEY, resource TEXT, username TEXT, password TEXT );");
		}

		@Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	} // end DSOH

	private final String TAG = "davsync";
	private Context context;
	private DSOH dsoh;

	// cache the application context in order to access application assets and resources
	public DSDatabase(Context context) {
		this.context = context;
		dsoh = new DSOH(context);
	}

	// save the server info to local storage
	public void addProfile(Profile p) {
		Log.d(TAG, "saving data...");
		SQLiteDatabase db = dsoh.getWritableDatabase();
		String q = "INSERT OR REPLACE INTO dav_profiles VALUES('"
			   + p.getHostname() + "','"
			   + p.getResource() + "','"
			   + p.getUsername() + "','"
			   + p.getPassword() + "');";
		db.execSQL(q);
		db.close();
	}

	public Profile getProfile() {
		Log.d(TAG, "retrieving profile...");
		// db = dsoh.getReadableDatabase(); db.close();
		return new Profile("h", "r", "u", "p");
	}

	// clear the fields and delete local storage
	public void delProfile(Profile p) {
		Log.d(TAG, "removing profile...");
	}

	// connect to the server & test credentials
	private void test() {
		Log.d(TAG, "testing connection...");
	}

}

