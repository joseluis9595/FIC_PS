package es.udc.psi1718.project.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.MySharedPrefsManager;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Panel;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.alertdialogs.MyCustomAlertDialog;
import es.udc.psi1718.project.view.customviews.pannelslist.PanelCursorAdapter;

public class MainActivity extends AppCompatActivity {

	public static Boolean active = false;
	private Context context = this;
	private Activity activity = this;
	private String TAG = "MainActivity";

	// Intents
	private int SETTINGSACTIV_REQUESTCODE = 2;
	private Intent controllersIntent;

	// Database helper
	private MySQLiteHelper mySQLiteHelper;

	// Layout variables
	private ListView lvPannels;
	private Toolbar myToolbar;
	private MyCustomAlertDialog myCustomAlertDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");

		// Add the toolbar
		myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// If first time opening the app, disable light-icon activity-alias
		if (MySharedPrefsManager.getInstance(context).isFirstTimeOpening()) {
			Util.disableComponent(getPackageManager(), new ComponentName(this, "es.udc.psi1718.project.LightIcon"));
		}

		// Show tutorial if necessary
		if (MySharedPrefsManager.getInstance(context).shouldShowTutorial()) {
			startTutorial(true);
		}

		// Database helper
		mySQLiteHelper = new MySQLiteHelper(this);


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
			if (resultCode == 0) {
				this.recreate();
			} else if (resultCode == Constants.INTENTCOMM_DONT_RECREATE) {
				this.finish();
			}

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
	public void onBackPressed() {
		if (myCustomAlertDialog.isOpened()) {
			myCustomAlertDialog.cancel();
			return;
		}
		super.onBackPressed();
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
				return true;
			case R.id.action_tutorial:
				startTutorial(false);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo mInfo) {
		super.onCreateContextMenu(menu, v, mInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int listPosition = info.position;
		Cursor cursor = (Cursor) lvPannels.getItemAtPosition(listPosition);
		int id = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_ID));

		switch (item.getItemId()) {
			case R.id.delete:
				inflateConfirmationDialog(id);
				return true;
			case R.id.update:
				inflateEditPanelDialog(listPosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		lvPannels = (ListView) findViewById(R.id.lv_mainactiv);

		// Add listener to the listView
		lvPannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long cursorID) {
				Log.d(TAG, "Pressed item " + i + " on the listview, cursorID : " + cursorID);
				// Intents
				controllersIntent = new Intent(context, PanelActivity.class);
				controllersIntent.putExtra(Constants.INTENTCOMM_PANELID, (int) cursorID);

				// TODO mejorar esto, poca independencia con base de datos
				Cursor selectedFromList = (Cursor) lvPannels.getItemAtPosition(i);
				String panelName = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_NAME));
				controllersIntent.putExtra(Constants.INTENTCOMM_PANELNAME, panelName);
				controllersIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// Add shared components animation for a more fluid UX
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					// Shared components we'll add to the animation
					View backgroundLayout = view.findViewById(R.id.panelitem_background);
					View statusBar = findViewById(android.R.id.statusBarBackground);
					View navigationBar = findViewById(android.R.id.navigationBarBackground);

					List<Pair<View, String>> pairs = new ArrayList<>();
					if (navigationBar != null) {
						pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
					}
					pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
					pairs.add(Pair.create(backgroundLayout, backgroundLayout.getTransitionName()));
					pairs.add(Pair.create((View) myToolbar, myToolbar.getTransitionName()));

					// Create the bundle with previous options
					Bundle optionsBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
							pairs.toArray(new Pair[pairs.size()])).toBundle();

					// Start activity
					startActivity(controllersIntent, optionsBundle);
				} else {
					startActivity(controllersIntent);
				}
			}
		});

		// Initialize listView with panels information
		refreshListView();

		// Context menu for the listview
		registerForContextMenu(lvPannels);

		// Initialize custom alertDialog layout
		initializeCustomDialog();
	}


	/**
	 * Initializes the custom dialog
	 */
	private void initializeCustomDialog() {
		myCustomAlertDialog = (MyCustomAlertDialog) findViewById(R.id.mycustomalertdialog);

		// Inflate the new view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newpanel_layout, null);
		myCustomAlertDialog.setView(dialogView);

		final EditText etNewPanelName = (EditText) dialogView.findViewById(R.id.et_newpanel_name);

		// Changing the title
		myCustomAlertDialog.setTitle(getString(R.string.alertdialog_newpanel_title));

		// Changing onClickListener
		myCustomAlertDialog.setPositiveClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Util.isEmptyEditText(etNewPanelName)) {
					// Don't dismiss the dialog
					Util.displayMessage(context, getString(R.string.err_completeallfields));
				} else {
					// Create a new controller
					mySQLiteHelper.insertPanel(new Panel(etNewPanelName.getText().toString()));
					refreshListView();
					etNewPanelName.setText("");
					myCustomAlertDialog.cancel();
				}
			}
		});

		// Changing onClickListener
		myCustomAlertDialog.setNegativeClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				etNewPanelName.setText("");
				myCustomAlertDialog.cancel();

			}
		});
	}


	/**
	 * Inflate a confirmation dialog before deleting a panel
	 *
	 * @param idPanel id of the panel that is going to be deleted
	 */
	private void inflateConfirmationDialog(final int idPanel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.alertdialog_title_deletepanel)
				.setMessage(R.string.alertdialog_message_deletepanel);

		builder.setPositiveButton(R.string.alertdialog_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deletePanel(idPanel);
			}
		});
		builder.setNegativeButton(R.string.alertdialog_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}


	/**
	 * Inflate a dialog to allow the user to edit the panel selected
	 *
	 * @param listPosition position of the list selected
	 */
	private void inflateEditPanelDialog(final int listPosition) {
		// Initialize variables
		Cursor cursor = (Cursor) lvPannels.getItemAtPosition(listPosition);
		final int panelId = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_ID));
		String panelName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_NAME));

		View customView = getLayoutInflater().inflate(R.layout.alertdialog_editcontroller, null);
		final EditText etName = (EditText) customView.findViewById(R.id.et_editcontroller_name);
		final EditText etPin = (EditText) customView.findViewById(R.id.et_editcontroller_pin);

		// Fill editTexts with the actual values of the controller
		etPin.setVisibility(View.GONE);

		// Create alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Customize the builder
		alertDialogBuilder.setTitle(R.string.alertdialog_title_editpanel)
				.setView(customView)
				.setPositiveButton(R.string.alertdialog_button_ok, null)
				.setNegativeButton(R.string.alertdialog_button_cancel, null);

		// Create and show the dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

		// Fill editTexts with the actual values of the controller
		etName.setText(panelName);

		// Override
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String newName = etName.getText().toString();

				// If empty fields display error
				if (Util.isEmptyString(newName)) {
					Toast.makeText(context, R.string.err_completeallfields, Toast.LENGTH_SHORT).show();
				} else {
					// Update controller in database
					mySQLiteHelper.updatePanelName(panelId, newName);

					// Close the dialog
					alertDialog.dismiss();

					// Refresh the listview
					refreshListView();
				}
			}
		});
	}


	/**
	 * Delete a panel given its id
	 *
	 * @param id id of the panel
	 */
	public void deletePanel(int id) {
		mySQLiteHelper.deletePanel(id);
		refreshListView();
	}


	/**
	 * Refresh the panel's listView
	 */
	private void refreshListView() {
		Cursor cursor = mySQLiteHelper.getAllPanels();
		lvPannels.setAdapter(new PanelCursorAdapter(context, cursor));

		// Uncomment these lines for debugging purposes
		// String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		// Log.e(TAG, cursorString);
	}

	/**
	 * Starts the tutorial activity and finished this one
	 */
	private void startTutorial(Boolean finishActivity) {
		Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
		startActivity(tutorialIntent);
		if (finishActivity)
			this.finish();
	}
}















