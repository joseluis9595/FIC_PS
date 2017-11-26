package es.udc.psi1718.project.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Panel;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.pannelslist.PanelCursorAdapter;

public class MainActivity extends AppCompatActivity {

	public static Boolean active = false;
	private Context context = this;
	private String TAG = "MainActivity";

	// Intents
	private int SETTINGSACTIV_REQUESTCODE = 2;
	private Intent controllersIntent;

	// Database helper
	private MySQLiteHelper mySQLiteHelper;

	// Layout variables
	private ListView lvPannels;
	private FloatingActionButton fabNewPanel;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");

		// TODO IT3 create a tutorial for first time opening

		// Prepare JSON file
		// MyJSONFileReader.getInstance().loadJsonFileAsync(this);

		// Database helper
		mySQLiteHelper = new MySQLiteHelper(this);

		// Intents
		controllersIntent = new Intent(context, ControllersActivity.class);

		// Initialize layout
		initializeLayout();
	}


	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		active = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult");
		if (requestCode == SETTINGSACTIV_REQUESTCODE) {
			this.recreate();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		active = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				Intent aboutIntent = new Intent(this, AboutActivity.class);
				startActivity(aboutIntent);
				return true;
			case R.id.action_settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, SETTINGSACTIV_REQUESTCODE);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		lvPannels = (ListView) findViewById(R.id.lv_mainactiv);
		fabNewPanel = (FloatingActionButton) findViewById(R.id.fab_mainactiv_newpanel);

		// Add listener to the fab
		fabNewPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "fab pressed");
				createNewPanelDialog();
			}
		});

		// Add listener to the listView
		lvPannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long cursorID) {
				Log.d(TAG, "Pressed item " + i + " on the listview, cursorID : " + cursorID);
				controllersIntent.putExtra(Constants.INTENTCOMM_PANNELID, (int) cursorID);
				startActivity(controllersIntent);
			}
		});

		// Initialize listView with pannels information
		refreshListView();
	}


	/**
	 * Refresh the panel's listView
	 */
	private void refreshListView() {
		Cursor cursor = mySQLiteHelper.getAllPanels();
		lvPannels.setAdapter(new PanelCursorAdapter(context, cursor));

		// TODO debug
		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);

		Cursor cursor1 = mySQLiteHelper.getControllersByPanelId(2);
		String cursorString1 = DatabaseUtils.dumpCursorToString(cursor1);
		Log.e(TAG, cursorString1);

	}

	/**
	 * Inflates an Alert Dialog to create a new panel
	 */
	private void createNewPanelDialog() {
		// Create AlertDialog builder
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		// Inflate and set the custom view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newpanel_layout, null);
		dialogBuilder.setView(dialogView);

		// Save the views inside the alertDialog
		final EditText etPanelName = (EditText) dialogView.findViewById(R.id.et_newpanel_name);

		// Set the rest of the options
		dialogBuilder
				.setCancelable(false)
				.setPositiveButton("Create", null)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});

		// Display AlertDialog
		final AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();

		// Override onClickListener so that we can control when alertDialog closes
		alertDialog
				.getButton(AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String controllerNameString = etPanelName.getText().toString();
						if (Util.isEmptyEditText(etPanelName)) {
							// Don't dismiss the dialog
							Util.displayMessage(context, getString(R.string.err_completeallfields));
						} else {
							// Create a new controller
							mySQLiteHelper.insertPanel(new Panel(etPanelName.getText().toString()));
							refreshListView();
							alertDialog.dismiss();
						}

					}
				});
	}
}















