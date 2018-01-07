package es.udc.psi1718.project.storage.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import es.udc.psi1718.project.storage.database.daos.Controller;
import es.udc.psi1718.project.storage.database.daos.Panel;


public class MySQLiteHelper extends SQLiteOpenHelper {
	private final static String TAG = "MySQLiteHelper";

	// DB constants
	// private static final String DB_NAME = "grades.db";
	private static final String DB_NAME = "easyarduino.db";
	private static final int DB_VERSION = 1;

	// DB tables
	public static final String TABLE_PANELS = "panels";
	public static final String TABLE_CONTROLLERS = "controllers";

	// DB columns
	public static final String COL_PANEL_ID = "_id";
	public static final String COL_PANEL_NAME = "panelName";

	public static final String COL_CONTROLLER_ID = "_id";
	public static final String COL_CONTROLLER_NAME = "controllerName";
	public static final String COL_CONTROLLER_CONTROLLERTYPE = "controllertype";
	public static final String COL_CONTROLLER_DATATYPE = "dataType";
	public static final String COL_CONTROLLER_PINTYPE = "pinType";
	public static final String COL_CONTROLLER_PINNUMBER = "pinNumber";
	public static final String COL_CONTROLLER_POSITION = "position";
	public static final String COL_CONTROLLER_PANELID = "panelId";
	public static final String COL_CONTROLLER_DATA = "data";
	// public static final String[] ALL_COL = {COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_GRADE};


	public MySQLiteHelper(Context context) {
		super(context, DB_NAME, new MySQLiteCursorFactory(true), DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		Log.d(TAG, "Creating DB");

		String TABLE_PANELS_CREATE = "create table "
				+ TABLE_PANELS + " ( "
				+ COL_PANEL_ID + " integer primary key autoincrement, "
				+ COL_PANEL_NAME + " text not null );";

		String TABLE_CONTROLLERS_CREATE = "create table "
				+ TABLE_CONTROLLERS + " ( "
				+ COL_CONTROLLER_ID + " integer primary key autoincrement, "
				+ COL_CONTROLLER_NAME + " text not null, "
				+ COL_CONTROLLER_CONTROLLERTYPE + " integer not null, "
				+ COL_CONTROLLER_DATATYPE + " text not null, "
				+ COL_CONTROLLER_PINTYPE + " text not null, "
				+ COL_CONTROLLER_PINNUMBER + " text not null, "
				+ COL_CONTROLLER_POSITION + " integer not null, "
				+ COL_CONTROLLER_PANELID + " integer not null, "
				+ COL_CONTROLLER_DATA + " integer not null, "
				+ "FOREIGN KEY (" + COL_CONTROLLER_PANELID + ") REFERENCES " + TABLE_PANELS + "(" + COL_PANEL_ID + ") ON DELETE CASCADE);";

		sqLiteDatabase.execSQL(TABLE_PANELS_CREATE);
		sqLiteDatabase.execSQL(TABLE_CONTROLLERS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading DB");
		Log.w(MySQLiteHelper.class.getName(), "Upgrading db from version "
				+ oldVersion + " to " + newVersion + ", which will destroy old data");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTROLLERS);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PANELS);
		onCreate(sqLiteDatabase);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.setForeignKeyConstraintsEnabled(true);
		}
	}

	/**
	 * Insert new panel into database
	 *
	 * @param panel New panel to be added
	 */
	public void insertPanel(Panel panel) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_PANEL_NAME, panel.getName());

		Log.d(TAG, "Inserting via DB object instance");
		db.insert(TABLE_PANELS, null, values);
	}

	/**
	 * Returns all panels in database
	 *
	 * @return Cursor
	 */
	public Cursor getAllPanels() {
		String query = "SELECT * FROM " + TABLE_PANELS;
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d(TAG, "getAllPanels via DB Object instance");

		Cursor cursor1 = db.rawQuery(query, null);

		String cursorString = DatabaseUtils.dumpCursorToString(cursor1);
		Log.e(TAG, cursorString);

		// Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_CONTROLLERS, null);
		// String cursorString2 = DatabaseUtils.dumpCursorToString(cursor2);
		// Log.e(TAG, cursorString2);

		return cursor1;
	}

	/**
	 * Update one panel's name
	 */
	public void updatePanelName(int panelId, String newName) {
		ContentValues values = new ContentValues();
		values.put(COL_PANEL_NAME, newName);

		getWritableDatabase().update(TABLE_PANELS, values,
				COL_PANEL_ID + " = " + panelId, null);
	}

	/**
	 * Deletes one panel from database
	 *
	 * @param id id of the panel
	 *
	 * @return integer
	 */
	public int deletePanel(int id) {
		Log.d(TAG, "Deleting via DB object instance");
		return getWritableDatabase().delete(TABLE_PANELS, COL_PANEL_ID + " =? ", new String[]{String.valueOf(id)});
	}


	/* 		CONTROLLERS 		*/


	/**
	 * Insert new controller into database
	 *
	 * @param controller New controller to be added
	 */
	public int insertController(Controller controller) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_CONTROLLER_NAME, controller.getName());
		values.put(COL_CONTROLLER_CONTROLLERTYPE, controller.getControllerType());
		values.put(COL_CONTROLLER_DATATYPE, controller.getDataType());
		values.put(COL_CONTROLLER_PINTYPE, controller.getPinType());
		values.put(COL_CONTROLLER_PINNUMBER, controller.getPinNumber());
		values.put(COL_CONTROLLER_POSITION, controller.getPosition());
		values.put(COL_CONTROLLER_PANELID, controller.getPanelId());
		values.put(COL_CONTROLLER_DATA, controller.getData());

		Log.d(TAG, "Inserting via DB object instance");
		return (int) db.insert(TABLE_CONTROLLERS, null, values);
	}


	/**
	 * Update one controller in the database
	 *
	 * @param controller controller with new parameters
	 */
	public void updateController(Controller controller) {
		ContentValues values = new ContentValues();
		values.put(COL_CONTROLLER_NAME, controller.getName());
		values.put(COL_CONTROLLER_CONTROLLERTYPE, controller.getControllerType());
		values.put(COL_CONTROLLER_DATATYPE, controller.getDataType());
		values.put(COL_CONTROLLER_PINTYPE, controller.getPinType());
		values.put(COL_CONTROLLER_PINNUMBER, controller.getPinNumber());
		values.put(COL_CONTROLLER_POSITION, controller.getPosition());
		values.put(COL_CONTROLLER_PANELID, controller.getPanelId());
		values.put(COL_CONTROLLER_DATA, controller.getData());

		getWritableDatabase().update(TABLE_CONTROLLERS, values,
				COL_CONTROLLER_ID + " = " + controller.getId(), null);
	}

	/**
	 * Updates just the data field of a controller
	 *
	 * @param controllerId id of the controller to edit
	 * @param data         new data
	 */
	public void updateControllerData(int controllerId, int data) {
		ContentValues values = new ContentValues();
		values.put(COL_CONTROLLER_DATA, data);

		getWritableDatabase().update(TABLE_CONTROLLERS, values,
				COL_CONTROLLER_ID + " = " + controllerId, null);
	}

	/**
	 * Returns all controllers on a panel given its id, sorted by panelId
	 *
	 * @param panelId id of the panel
	 *
	 * @return List of {@link Controller}
	 */
	public ArrayList<Controller> getControllersByPanelId(int panelId) {
		String selectionText = COL_CONTROLLER_PANELID + " =? ";
		String[] selectionItems = new String[]{String.valueOf(panelId)};

		// Initialize 'Sort' variables
		// Using COLLATE LOCALIZED to allow db to sort items using special characters
		String orderCriteria = COL_CONTROLLER_POSITION + " COLLATE LOCALIZED ASC";

		// Execute sentence in database
		Cursor cursor = getWritableDatabase().query(TABLE_CONTROLLERS, null,
				selectionText, selectionItems,
				null, null, orderCriteria);

		// Check if cursor is empty
		if (cursor == null || cursor.getCount() <= 0) {
			Log.e(TAG, "getControllersByPanelId : Cursor is empty");
			return null;
		}

		// Print the cursor for debugging purposes
		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);

		// Create a new Controllers list
		ArrayList<Controller> controllers = new ArrayList<>();

		// Populate the arrayList
		if (cursor.moveToFirst()) {
			do {
				// Get controller fields
				int id = cursor.getInt(cursor.getColumnIndex(COL_CONTROLLER_ID));
				String name = cursor.getString(cursor.getColumnIndex(COL_CONTROLLER_NAME));
				int controllerType = cursor.getInt(cursor.getColumnIndex(COL_CONTROLLER_CONTROLLERTYPE));
				String dataType = cursor.getString(cursor.getColumnIndex(COL_CONTROLLER_DATATYPE));
				String pinType = cursor.getString(cursor.getColumnIndex(COL_CONTROLLER_PINTYPE));
				String pinNumber = cursor.getString(cursor.getColumnIndex(COL_CONTROLLER_PINNUMBER));
				int position = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CONTROLLER_POSITION));
				int data = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CONTROLLER_DATA));

				// Add a new Controller object to the list
				controllers.add(new Controller(id, name, controllerType, dataType, pinType, pinNumber, position, panelId, data));
			} while (cursor.moveToNext());
		}
		// Close the cursor
		cursor.close();

		// Return populated list
		return controllers;
	}


	/**
	 * Update indexes of controllers (useful when controllers moved using drag & drop)
	 *
	 * @param initialPosition initial position of the controller
	 * @param finalPosition   final position of the controller
	 */
	public void updateIndexes(int panelId, int initialPosition, int finalPosition) {
		if (initialPosition == finalPosition) return;

		// Find item in position 'initialPosition'
		int controllerID = findControllerByPosition(panelId, initialPosition);

		// Check which was the movement of the controller
		if (initialPosition < finalPosition) {
			// Update items with position initialPosition < pos <= finalPosition to pos-1
			getWritableDatabase().execSQL("UPDATE " + TABLE_CONTROLLERS +
							" SET " + COL_CONTROLLER_POSITION + "=" + COL_CONTROLLER_POSITION + "-1" +
							" WHERE " + COL_CONTROLLER_POSITION + ">? AND " + COL_CONTROLLER_POSITION +
							"<=? AND " + COL_CONTROLLER_PANELID + "=?",
					new String[]{
							String.valueOf(initialPosition),
							String.valueOf(finalPosition),
							String.valueOf(panelId)
					});
		} else {
			// Update items with position finalPosition <= pos < initialPosition to pos+1
			getWritableDatabase().execSQL("UPDATE " + TABLE_CONTROLLERS +
							" SET " + COL_CONTROLLER_POSITION + "=" + COL_CONTROLLER_POSITION + "+1" +
							" WHERE " + COL_CONTROLLER_POSITION + ">=? AND " + COL_CONTROLLER_POSITION +
							"<? AND " + COL_CONTROLLER_PANELID + "=?",
					new String[]{
							String.valueOf(finalPosition),
							String.valueOf(initialPosition),
							String.valueOf(panelId)
					});
		}

		// Update main item, set new position to finalPosition
		getWritableDatabase().execSQL("UPDATE " + TABLE_CONTROLLERS +
						" SET " + COL_CONTROLLER_POSITION + "=" + finalPosition +
						" WHERE " + COL_CONTROLLER_ID + "=? AND " + COL_CONTROLLER_PANELID + "=?",
				new String[]{
						String.valueOf(controllerID),
						String.valueOf(panelId)
				});

		// TODO debug
		// Cursor cursor = getControllersByPanelId(panelId);
		// Log.e(TAG, DatabaseUtils.dumpCursorToString(cursor));
	}


	/**
	 * Find one controller given its position
	 *
	 * @param position position of the controller
	 *
	 * @return id of the controller
	 */
	private int findControllerByPosition(int panelId, int position) {
		String selectionText = COL_CONTROLLER_POSITION + " =? AND " + COL_CONTROLLER_PANELID + "=?";
		String[] selectionItems = new String[]{
				String.valueOf(position),
				String.valueOf(panelId)
		};

		// Execute sentence in database
		Cursor cursor = getWritableDatabase().query(TABLE_CONTROLLERS, null,
				selectionText, selectionItems,
				null, null, null);
		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_CONTROLLER_ID));
	}


	/**
	 * Deletes one controller from database
	 *
	 * @param id id of the controller
	 *
	 * @return integer
	 */
	public int deleteController(int id) {
		Log.d(TAG, "Deleting via DB object instance");
		return getWritableDatabase().delete(TABLE_CONTROLLERS, COL_CONTROLLER_ID + " =? ", new String[]{String.valueOf(id)});
	}

}






























