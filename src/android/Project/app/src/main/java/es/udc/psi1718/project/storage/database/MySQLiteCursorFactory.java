package es.udc.psi1718.project.storage.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

/* Info taken from : https://stackoverflow.com/questions/5966584/logging-sql-queries-in-android */
/* Class created to be able to debug SQLite sentences via log */

public class MySQLiteCursorFactory implements SQLiteDatabase.CursorFactory {

	private boolean debugQueries = false;
	private String TAG = "MySQLiteCursorFactory";

	public MySQLiteCursorFactory() {
		this.debugQueries = false;
	}

	public MySQLiteCursorFactory(boolean debugQueries) {
		this.debugQueries = debugQueries;
	}

	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
							String editTable, SQLiteQuery query) {
		if (debugQueries) {
			Log.e(TAG, query.toString());
		}
		return new SQLiteCursor(db, masterQuery, editTable, query);
	}
}
