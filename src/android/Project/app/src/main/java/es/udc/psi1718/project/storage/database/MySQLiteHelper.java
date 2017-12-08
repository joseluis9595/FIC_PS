package es.udc.psi1718.project.storage.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import es.udc.psi1718.project.storage.database.daos.Controller;
import es.udc.psi1718.project.storage.database.daos.Panel;


public class MySQLiteHelper extends SQLiteOpenHelper {
	private final static String TAG = "MySQLiteHelper";

	private final Boolean USE_CONTENT_RESOLVER = true;

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
				+ COL_CONTROLLER_PANELID + " integer not null );";
		// TODO añadir clave foránea en CONTROLLERS_PANELID

		sqLiteDatabase.execSQL(TABLE_PANELS_CREATE);
		sqLiteDatabase.execSQL(TABLE_CONTROLLERS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading DB");
		Log.w(MySQLiteHelper.class.getName(), "Upgrading db from version "
				+ oldVersion + " to " + newVersion + ", which will destroy old data");
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PANELS);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTROLLERS);
		onCreate(sqLiteDatabase);
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
		return db.rawQuery(query, null);
	}

	/**
	 * Deletes one panel from database
	 *
	 * @param id id of the grade
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
	public void insertController(Controller controller) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_CONTROLLER_NAME, controller.getName());
		values.put(COL_CONTROLLER_CONTROLLERTYPE, controller.getControllerType());
		values.put(COL_CONTROLLER_DATATYPE, controller.getDataType());
		values.put(COL_CONTROLLER_PINTYPE, controller.getPinType());
		values.put(COL_CONTROLLER_PINNUMBER, controller.getPinNumber());
		values.put(COL_CONTROLLER_POSITION, controller.getPosition());
		values.put(COL_CONTROLLER_PANELID, controller.getPanelId());

		Log.d(TAG, "Inserting via DB object instance");
		db.insert(TABLE_CONTROLLERS, null, values);
	}


	/**
	 * Returns all controllers on a panel given its id, sorted by panelId
	 *
	 * @param panelId id of the panel
	 *
	 * @return Cursor with all the controllers
	 */
	public Cursor getControllersByPanelId(int panelId) {
		String selectionText = COL_CONTROLLER_PANELID + " =? ";
		String[] selectionItems = new String[]{String.valueOf(panelId)};

		// Initialize 'Sort' variables
		// Using COLLATE LOCALIZED to allow db to sort items using special characters
		String orderCriteria = COL_CONTROLLER_POSITION + " COLLATE LOCALIZED ASC";

		// Execute sentence in database
		Log.d(TAG, "GETGRADES via DB Object instance");
		return getWritableDatabase().query(TABLE_CONTROLLERS, null,
				selectionText, selectionItems,
				null, null, orderCriteria);
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
		Cursor cursor = getControllersByPanelId(panelId);
		Log.e(TAG, DatabaseUtils.dumpCursorToString(cursor));
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
		Log.d(TAG, "GETGRADES via DB Object instance");
		Cursor cursor = getWritableDatabase().query(TABLE_CONTROLLERS, null,
				selectionText, selectionItems,
				null, null, null);
		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_CONTROLLER_ID));
	}


	//
	//
	// /**
	//  * Deletes one grade from database
	//  *
	//  * @param id id of the grade
	//  *
	//  * @return integer
	//  */
	// public int deleteGrade(int id) {
	// 	Log.d(TAG, "Deleting via DB object instance");
	// 	return getWritableDatabase().delete(TABLE_GRADES, COL_ID + " =? ", new String[]{String.valueOf(id)});
	// }
	//
	//
	// /**
	//  * Updates one grade in the database
	//  *
	//  * @param grade grade to be updated
	//  */
	// public void updateGrade(Grade grade) {
	// 	ContentValues values = new ContentValues();
	// 	values.put(COL_FIRSTNAME, grade.getFirstName());
	// 	values.put(COL_LASTNAME, grade.getLastName());
	// 	values.put(COL_GRADE, grade.getGrade());
	//
	// 	Log.d(TAG, "Updating via DB object instance");
	// 	getWritableDatabase().update(TABLE_GRADES, values, COL_ID + " = " + grade.getId(), null);
	//
	// }
	//
	//
	// /**
	//  * Returns all grades in database
	//  *
	//  * @return Cursor
	//  */
	// public Cursor getAllGrades() {
	// 	String query = "SELECT * FROM " + TABLE_GRADES;
	// 	SQLiteDatabase db = this.getReadableDatabase();
	// 	Log.d(TAG, "GETALLGRADES via DB Object instance");
	// 	return db.rawQuery(query, null);
	// }
	//
	// /**
	//  * Return grades from database
	//  *
	//  * @param order          order
	//  * @param columnToOrder  which column is used to order data
	//  * @param searchCriteria filter by text
	//  * @param columnToSearch which column to search the 'searchCriteria' text
	//  *
	//  * @return Cursor
	//  */
	// public Cursor getGrades(String order, String columnToOrder,
	// 						String searchCriteria, String columnToSearch) {
	//
	// 	String selectionText;
	// 	String[] selectionItems;
	// 	String orderCriteria;
	//
	// 	// Initialize 'Search' variables
	// 	if (searchCriteria == null || columnToSearch == null || Util.isEmptyString(searchCriteria)) {
	// 		selectionText = null;
	// 		selectionItems = null;
	// 	} else {
	// 		// If we are searching ID or grade, search the exact value
	// 		if (columnToSearch.equals(COL_GRADE) || columnToSearch.equals(COL_ID)) {
	// 			selectionText = columnToSearch + " =? ";
	// 			selectionItems = new String[]{searchCriteria};
	// 		} else {
	// 			selectionText = columnToSearch + " LIKE ? ";
	// 			selectionItems = new String[]{"%" + searchCriteria + "%"};
	// 		}
	// 	}
	//
	// 	// Initialize 'Sort' variables
	// 	if (order == null || columnToOrder == null) {
	// 		orderCriteria = null;
	// 	} else {
	// 		// Using COLLATE LOCALIZED to allow db to sort items using special characters
	// 		orderCriteria = columnToOrder + " COLLATE LOCALIZED " + order;
	// 	}
	//
	// 	// Execute sentence in database
	// 	Log.d(TAG, "GETGRADES via DB Object instance");
	// 	return getWritableDatabase().query(TABLE_GRADES, null,
	// 			selectionText, selectionItems,
	// 			null, null,
	// 			orderCriteria);
	// }
	//
	// /**
	//  * Returns one grade given its id
	//  *
	//  * @param id id of the grade
	//  *
	//  * @return Grade
	//  */
	// public Grade getGradeById(Integer id) {
	// 	Cursor cursor;
	// 	Log.d(TAG, "GETGRADEBYID via DB Object instance");
	// 	cursor = getWritableDatabase().query(TABLE_GRADES, null, COL_ID + " =? ", new String[]
	// 			{String.valueOf(id)}, null, null, null);
	//
	// 	// If the cursor has any data
	// 	if ((cursor != null) && (cursor.getCount() > 0)) {
	// 		// Move cursor to first position
	// 		cursor.moveToFirst();
	//
	// 		// Get data from cursor
	// 		int gradeId = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_ID));
	// 		String name = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_FIRSTNAME));
	// 		String lastName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_LASTNAME));
	// 		int gradeValue = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_GRADE));
	//
	// 		// Close the cursor
	// 		cursor.close();
	//
	// 		// Create a new Grade object and return it
	// 		return new Grade(gradeId, name, lastName, gradeValue);
	// 	}
	// 	return null;
	//
	// }

}






























