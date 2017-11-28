package es.udc.psi1718.project.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import es.udc.psi1718.project.MyBroadcastReceiver;
import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.arduinomanager.ArduinoResponseCodes;
import es.udc.psi1718.project.arduinomanager.ArduinoSerialListener;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Controller;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.controllers.ControllerSliderView;
import es.udc.psi1718.project.view.customviews.controllers.ControllerSwitchView;
import es.udc.psi1718.project.view.customviews.controllers.ControllerViewEventListener;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridLayout;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridListener;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public class ControllersActivity extends AppCompatActivity implements ArduinoSerialListener, ControllerViewEventListener, ControllersGridListener {

	private Context context = this;
	private String TAG = "ControllersActivity";
	public static Boolean active = false;

	private final Boolean DEBUG = true;        // TODO DEBUG remove this constant

	// Panel variables
	private int panelId;

	// Database access
	private MySQLiteHelper mySQLiteHelper;

	// Layout variables
	private FloatingActionButton fab;
	private Button buttonStartComm;
	private TextView loadingTextView;
	private ProgressBar progressBar;
	private ControllersGridLayout customGridLayout;
	private RelativeLayout loadingLayout;
	private RelativeLayout normalLayout;

	// Arduino communication Manager
	private ArduinoCommunicationManager arduinoCommunication;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_controllers);
		initializeLayout();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_controllers);
		Log.d(TAG, "ONCREATE");

		// TODO tutorial, indicar como mover controllers de sitio, como crearlos, etc...


		// Intents
		panelId = getIntent().getIntExtra(Constants.INTENTCOMM_PANELID, -1);
		String panelName = getIntent().getStringExtra(Constants.INTENTCOMM_PANELNAME);
		if (panelId == -1) {
			Log.e(TAG, "Invalid pannel id");
			this.finish();
		} else {
			android.support.v7.app.ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(panelName != null ? panelName : "Panel");
			}
		}

		// Database
		mySQLiteHelper = new MySQLiteHelper(context);

		// Arduino communication
		arduinoCommunication = new ArduinoCommunicationManager(context);

		// USB attached/detached broadacast Receiver
		broadcastReceiver = new MyBroadcastReceiver(this, arduinoCommunication);
		intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
		intentFilter.addAction(ACTION_USB_DEVICE_DETACHED);

		// Initialize layout
		initializeLayout();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "ONSTART");

		// Register USB receiver
		Log.e(TAG, "onStart : REGISTER RECEIVER USB");
		registerReceiver(broadcastReceiver, intentFilter);

		// Start communication
		startCommunication();

		// Set active flag to true
		active = true;

		// Check if the activity was called from the broadcast receiver
		// Bundle extras = getIntent().getExtras();
		// if (extras == null) return;
		// Boolean fromBroadcastReceiver = extras.getBoolean(Constants.INTENTCOMM_CONTACTIV_LAUNCHEDFROMBR, false);
		// if (fromBroadcastReceiver) {
		// 	Log.d(TAG, "Starting activity from broadcasReceiver");
		// 	startCommunication();
		// }
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
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
		// TODO saveControllersState();
		// Set active flag to false
		active = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		Log.e(TAG, "onDestroy : UNREGISTER RECEIVER USB");

		// TODO Unregister broadcast receiver
		try {
			unregisterReceiver(broadcastReceiver);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		// End communication with Arduino
		endCommunication();

		// TODO IT2 remove - Set result for the activity (only for IT1)
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.e(TAG, "onBackPressed");
		// finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// setResult(RESULT_OK, null);
			finish();
		}
		return true;
	}

	/**
	 * Gets previous saved controllers and displays them on the sc
	 */
	private void loadSavedControllers() {
		// TODO DEBUG remove this line when not debugging
		// Create new Controller for test purposes
		// createNewController("Controller prueba con nombre muy largo que ocupe al menos dos líneas", "8", "Digital", "Write");
		// createNewController("Prueba2", "9", "Analog", "Write");
		// createNewController("Prueba3", "9", "Analog", "Write");
		// createNewController("Ja", "8", "Digital", "Write");

		// Remove previous views from the layout
		customGridLayout.reset();

		// Get previous saved controllers
		Cursor cursor = mySQLiteHelper.getControllersByPanelId(panelId);
		if (cursor == null || cursor.getCount() <= 0) {
			Log.e(TAG, "loadSavedControllers : Cursor is empty");
			return;
		}

		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);
		if (cursor.moveToFirst()) {
			do {
				Log.e(TAG, "Creating new controller");
				String name = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_NAME));
				String dataType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_DATATYPE));
				String pinType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_PINTYPE));
				String pinNumber = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_PINNUMBER));
				// int position = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_CONTROLLER_POSITION));
				createNewController(name, pinNumber, pinType, dataType);
			} while (cursor.moveToNext());
		}
		cursor.close();


		// try {
		// 	Log.e(TAG, "loadSavedControllers : Cursor is not empty");
		// 	cursor.moveToFirst();
		// 	while (cursor.moveToNext()) {
		// 		Log.e(TAG, "Creating new controller");
		// 		String name = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_PANEL_NAME));
		// 		String dataType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_DATATYPE));
		// 		String pinType = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_PINTYPE));
		// 		String pinNumber = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COL_CONTROLLER_PINNUMBER));
		// 		// int position = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_CONTROLLER_POSITION));
		// 		createNewController(name, pinNumber, pinType, dataType);
		// 	}
		// } finally {
		// 	cursor.close();
		// }
	}


	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		// Initialize layout variables
		fab = (FloatingActionButton) findViewById(R.id.fab_new_controller);
		// mainLinearLayout = (LinearLayout) findViewById(R.id.controllers_main_layout);
		customGridLayout = (ControllersGridLayout) findViewById(R.id.customgrid);
		loadingLayout = (RelativeLayout) findViewById(R.id.controllers_loading_layout);
		normalLayout = (RelativeLayout) findViewById(R.id.controllers_parent_layout);
		loadingTextView = (TextView) findViewById(R.id.controllers_loading_text_view);
		buttonStartComm = (Button) findViewById(R.id.controllers_retry_button);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		// Create one common listener
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.fab_new_controller:
						createNewControllerDialog();
						break;
					case R.id.controllers_retry_button:
						// TODO DEBUG uncomment this when not debugging
						if (!DEBUG) {
							startCommunication();
						}
						break;
					default:
						break;
				}
			}
		};


		// Add listener to different layout items
		fab.setOnClickListener(onClickListener);
		buttonStartComm.setOnClickListener(onClickListener);

		// Restore previous Controllers
		loadSavedControllers();

		// By default, layout is disabled
		// TODO DEBUG uncomment this when not debugging
		if (DEBUG) {
			enableUI();
		} else {
			disableUI();
			setLoading(false);
		}


	}


	/**
	 * Creates a new Controller layout
	 */
	private void createNewController(String name, String arduinoPin, String pinType, String dataType) {

		// Pin de escritura digital
		if (pinType.equalsIgnoreCase("digital") && dataType.equalsIgnoreCase("write")) {
			Log.d(TAG, "Creating new controller : digWrite");
			ControllerSwitchView controllerSwitchView = new ControllerSwitchView(context, name, arduinoPin, pinType, dataType);
			// mainLinearLayout.addView(controllerSwitchView.getView());
			// customGridLayout.addCard(controllerSwitchView.getView());
			customGridLayout.addController(controllerSwitchView);
			return;
		}

		// Pin de escritura analógica
		if (pinType.equalsIgnoreCase("analog") && dataType.equalsIgnoreCase("write")) {
			Log.d(TAG, "Creating new controller : anWrite");
			ControllerSliderView controllerSliderView = new ControllerSliderView(context, name, arduinoPin, pinType, dataType);
			// mainLinearLayout.addView(controllerSliderView.getView());
			// customGridLayout.addCard(controllerSliderView.getView());
			customGridLayout.addController(controllerSliderView);
			return;
		}

		// Pin de lectura
		if (dataType.equalsIgnoreCase("read")) {
			Log.d(TAG, "Creating new controller : read");
			// TODO IT3 Crear un cardview de lectura de valores
			return;
		}

		Log.d(TAG, "Creating new controller : unknown");
	}


	/**
	 * Inflates an Alert Dialog to create a new controller
	 */
	private void createNewControllerDialog() {
		// Create AlertDialog builder
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		// Inflate and set the custom view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newcontroller_layout, null);
		dialogBuilder.setView(dialogView);

		// Save the views inside the alertDialog
		final EditText newControllerEditText = (EditText) dialogView.findViewById(R.id.new_controller_name_edit_text);
		final EditText pinNumberEditText = (EditText) dialogView.findViewById(R.id.new_controller_pin_number_edit_text);
		final Spinner pinTypeSpinner = (Spinner) dialogView.findViewById(R.id.new_controller_pin_type_spinner);
		final Spinner dataTypeSpinner = (Spinner) dialogView.findViewById(R.id.new_controller_data_type_spinner);

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
		Log.d(TAG, "createNewControllerDialog : created AlertDialog");

		// Override onClickListener so that we can control when alertDialog closes
		alertDialog
				.getButton(AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String controllerNameString = newControllerEditText.getText().toString();
						String arduinoPinString = pinNumberEditText.getText().toString();

						// Check if the user completed all the fields
						if ((!Util.isEmptyString(controllerNameString)) && (!Util.isEmptyString(arduinoPinString))) {

							// TODO IT3 remove this line when readData controllers have been created
							if (dataTypeSpinner.getSelectedItem().toString().equalsIgnoreCase("read")) {
								Toast.makeText(context
										, "Error creating 'Read' controller, " + getString(R.string.err_not_implemented_yet)
										, Toast.LENGTH_SHORT).show();
								return;
							}

							// Create new controller
							Controller controller = new Controller(
									controllerNameString,
									dataTypeSpinner.getSelectedItem().toString(),
									pinTypeSpinner.getSelectedItem().toString(),
									arduinoPinString,
									customGridLayout.getControllersCount(),
									panelId
							);
							mySQLiteHelper.insertController(controller);

							// Refresh view
							createNewController(
									controllerNameString,
									arduinoPinString,
									pinTypeSpinner.getSelectedItem().toString(),
									dataTypeSpinner.getSelectedItem().toString());

							// Dismiss the dialog
							alertDialog.dismiss();
						} else {
							// Don't dismiss the dialog
							Util.displayMessage(context, getString(R.string.err_completeallfields));
						}
					}
				});
	}


	/**
	 * Disables UI, user can only start communication with arduino
	 */
	private void enableUI() {
		loadingLayout.setVisibility(View.GONE);
		normalLayout.setVisibility(View.VISIBLE);
		//buttonStartComm.setEnabled(false);
		//buttonSendCommand.setEnabled(true);
		//editText.setEnabled(true);
	}


	/**
	 * Display loading layout
	 *
	 * @param loading Boolean to indicate whether it's loading or not
	 */
	private void setLoading(Boolean loading) {
		String loadingText = loading ? "Loading..." : "No devices found";
		int progressBarVisibility = loading ? View.VISIBLE : View.GONE;

		buttonStartComm.setEnabled(!loading);
		loadingTextView.setText(loadingText);
		progressBar.setVisibility(progressBarVisibility);
	}


	/**
	 * Enables UI so that the user can interact with the application
	 */
	private void disableUI() {
		loadingLayout.setVisibility(View.VISIBLE);
		normalLayout.setVisibility(View.GONE);
		//buttonStartComm.setEnabled(true);
		//buttonSendCommand.setEnabled(false);
		//editText.setEnabled(false);
	}


	/**
	 * Start communication with Arduino
	 */
	public void startCommunication() {
		Log.d(TAG, "StartCommunication");

		ArduinoResponseCodes responseCode = arduinoCommunication.startCommunication();
		if (responseCode.getCode() <= 0 && !responseCode.equals(ArduinoResponseCodes.ERROR_NO_DEVICE)) {
			Util.displayError(context, responseCode.getDescription());
		} else if (responseCode.equals(ArduinoResponseCodes.ERROR_NO_DEVICE)) {
			Log.e(TAG, "No devices found");
		} else {
			Log.d(TAG, "StartCommunication : OK");
			setLoading(true);
		}
	}


	/**
	 * End communication with Arduino
	 */
	private void endCommunication() {
		arduinoCommunication.closeConnection();
		// if (responseCode.getCode() <= 0) {
		// 	Util.displayError(context, responseCode.getDescription());
		// }
	}


	/**
	 * Sends command via Serial Port
	 */
	private void sendCommand(String arduinoPin, int pinType, int dataType, int data) {
		ArduinoResponseCodes responseCode = arduinoCommunication.sendCommand(arduinoPin, pinType, dataType, data);
		if (responseCode.getCode() <= 0) {
			Util.displayError(context, responseCode.getDescription());
		}
	}





	/* ARDUINO SERIAL LISTENER FUNCTIONS */

	@Override
	public void receivedData(String data) {
		Log.d(TAG, "RECEIVEDDATA : Data received - " + data);
		final String auxData = data;

		// runOnUiThread(new Runnable() {
		// 	@Override
		// 	public void run() {
		// 		Util.displayMessage(context, auxData);
		// 	}
		// });
	}

	@Override
	public void connectionOpened() {
		Log.d(TAG, "connectionOpened : OK");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO IT2 comprobar si se ha cancelado la comunicación
				enableUI();
				// TODO DEBUG Util.displayMessage(context, "Connection opened!");
			}
		});
	}

	@Override
	public void connectionClosed(ArduinoResponseCodes responseCode) {
		// TODO IT2-3 delete all saved state values (like switches or sliders state)
		Log.d(TAG, "Connection closed");
		final ArduinoResponseCodes finalCode = responseCode;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (finalCode.getCode() > 0) {
					// TODO DEBUG Util.displayMessage(context, "Connection closed");
				}
				disableUI();
				setLoading(false);
			}
		});
	}

	@Override
	public void connectionFailed(final ArduinoResponseCodes arduinoResponseCode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableUI();
				Util.displayError(context, arduinoResponseCode.getDescription());
				setLoading(false);
			}
		});
	}


	/* CONTROLLER VIEW LISTENER FUNCTIONS */

	@Override
	public void controllerSentCommand(final String arduinoPin, final int pinType, final int dataType, final int data) {
		Log.d(TAG, "ControllerChangeState : Sending command to Arduino");
		sendCommand(arduinoPin, pinType, dataType, data);
	}

	@Override
	public void controllersPositionChanged(int initialPosition, int finalPosition) {
		mySQLiteHelper.updateIndexes(panelId, initialPosition, finalPosition);
	}
}
