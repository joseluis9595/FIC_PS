package es.udc.psi1718.project.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.AbstractArduinoCommunicationManager;
import es.udc.psi1718.project.arduinomanager.ArduinoResponseCodes;
import es.udc.psi1718.project.arduinomanager.ArduinoSerialConnectionListener;
import es.udc.psi1718.project.arduinomanager.ArduinoUSBCommunicationManager;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Controller;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.alertdialogs.MyCustomAlertDialog;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridLayout;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridListener;
import es.udc.psi1718.project.view.customviews.controllersgrid.controllers.ControllerView;
import es.udc.psi1718.project.view.customviews.controllersgrid.controllers.ControllerViewEventListener;
import es.udc.psi1718.project.view.customviews.controllersgrid.controllers.ControllerViewManager;

public class ControllersActivity extends AppCompatActivity implements ArduinoSerialConnectionListener, ControllerViewEventListener, ControllersGridListener {

	private Context context = this;
	private Activity activity = this;
	private String TAG = "ControllersActivity";
	public static Boolean active = false;

	// Panel variables
	private int panelId;
	private String panelName;

	// Database access
	private MySQLiteHelper mySQLiteHelper;

	// Layout variables
	private MyCustomAlertDialog myCustomAlertDialog;
	private EditText newControllerEditText, pinNumberEditText;
	private Spinner pinTypeSpinner, dataTypeSpinner;
	private Spinner controllerTypeSpinner;

	// Custom layout variables
	private ControllersGridLayout customGridLayout;
	private ControllerViewManager controllerViewManager;

	// Arduino communication Manager
	private AbstractArduinoCommunicationManager arduinoCommunication;
	// private BroadcastReceiver broadcastReceiver;
	// private IntentFilter intentFilter;
	private Boolean connectionIsActive = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (connectionIsActive || Constants.DEBUG) {
			this.recreate();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_controllers);
		Log.d(TAG, "ONCREATE");

		// Set active flag to true
		active = true;

		// TODO tutorial, indicar como mover controllers de sitio, como crearlos, etc...

		// Intent communication
		panelId = getIntent().getIntExtra(Constants.INTENTCOMM_PANELID, -1);
		panelName = getIntent().getStringExtra(Constants.INTENTCOMM_PANELNAME);
		if (panelId == -1) {
			Log.e(TAG, "Invalid pannel id");
			this.finish();
		}

		// Managers
		mySQLiteHelper = new MySQLiteHelper(context);
		controllerViewManager = new ControllerViewManager(this);

		// Arduino communication
		// arduinoCommunication = ArduinoCommunicationManager.getInstance(context);
		arduinoCommunication = new ArduinoUSBCommunicationManager(context);

		// USB attached/detached broadacast Receiver
		// broadcastReceiver = new MyBroadcastReceiver(this, arduinoCommunication);
		// intentFilter = new IntentFilter();
		// intentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
		// intentFilter.addAction(ACTION_USB_DEVICE_DETACHED);

		// Initialize layout
		initializeLayout();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "ONSTART");

		// Set active flag to true
		active = true;

		// Register USB receiver
		Log.e(TAG, "onStart : REGISTER RECEIVER USB");
		// registerReceiver(broadcastReceiver, intentFilter);

		// Start communication
		if (!connectionIsActive) startCommunication();
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
		saveControllersState();
		controllerViewManager.endControllers();
		// Set active flag to false
		active = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		Log.e(TAG, "onDestroy : UNREGISTER RECEIVER USB");

		// Unregister broadcast receiver
		// try {
		// 	unregisterReceiver(broadcastReceiver);
		// } catch (Exception e) {
		// 	Log.e(TAG, e.toString());
		// }

		// End communication with Arduino
		endCommunication();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Log.e(TAG, "onBackPressed");
		if (myCustomAlertDialog.isOpened()) {
			myCustomAlertDialog.cancel();
		} else supportFinishAfterTransition();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home) {
			supportFinishAfterTransition();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * Gets previous saved controllers and displays them on the sc
	 */
	private void loadSavedControllers() {
		// Remove previous views from the layout
		customGridLayout.reset();

		// Get the list of controllers
		ArrayList<Controller> controllers = mySQLiteHelper.getControllersByPanelId(panelId);
		if (controllers == null || controllers.isEmpty()) {
			Log.i(TAG, "loadSavedControllers : Controllers list is empty");
			return;
		}

		// For every item in the list, create a new controller view
		for (Controller controller : controllers) {
			addControllerView(controller);
		}
	}


	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		// Add the toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// Add home button in the toolbar
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(panelName != null ? panelName : "Panel");
		}

		// Initialize layout variables
		customGridLayout = (ControllersGridLayout) findViewById(R.id.customgrid);

		// Restore previous Controllers
		loadSavedControllers();

		// If connection is opened, enable controllers
		if (connectionIsActive) {
			controllerViewManager.startControllers();
		}

		// Initialize custom alert dialog layout
		initializeCustomDialog();
	}


	/**
	 * Initialize the custom dialog inner view
	 *
	 * @return View
	 */
	private View initializeCustomAlertDialogView() {
		// Inflate the new view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newcontroller_layout, null);

		// TODO move to array variable
		final String[] controllersType = new String[]{
				getString(R.string.controllertype_led),
				getString(R.string.controllertype_servo),
				getString(R.string.controllertype_tempsensor),
				getString(R.string.controllertype_humidsensor),
				getString(R.string.controllertype_generalcontroller),
				getString(R.string.controllertype_lightsensor)};

		newControllerEditText = (EditText) dialogView.findViewById(R.id.et_newcontroller_name);
		pinNumberEditText = (EditText) dialogView.findViewById(R.id.et_newcontroller_pinnumber);
		controllerTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_type);
		pinTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_pintype);
		dataTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_datatype);
		final LinearLayout layoutPinType = (LinearLayout) dialogView.findViewById(R.id.layout_newcontroller_pintype);
		final LinearLayout layoutDataType = (LinearLayout) dialogView.findViewById(R.id.layout_newcontroller_datatype);

		// Set spinner entries
		ArrayAdapter<String> controllersTypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, controllersType);
		controllersTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		controllerTypeSpinner.setAdapter(controllersTypeAdapter);

		// Create listener for the controllerType spinner
		final AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				String itemSelected = controllersType[position];
				if (itemSelected.equals(getString(R.string.controllertype_led))) {
					// Toast.makeText(context, "LED selected", Toast.LENGTH_SHORT).show();
					layoutDataType.setVisibility(View.GONE);
					layoutPinType.setVisibility(View.VISIBLE);
				} else if (itemSelected.equals(getString(R.string.controllertype_generalcontroller))) {
					// Toast.makeText(context, "General purpose one", Toast.LENGTH_SHORT).show();
					layoutDataType.setVisibility(View.VISIBLE);
					layoutPinType.setVisibility(View.VISIBLE);
				} else {
					// Toast.makeText(context, "normal ones selected", Toast.LENGTH_SHORT).show();
					layoutDataType.setVisibility(View.GONE);
					layoutPinType.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		};

		// Add listener to the spinner
		controllerTypeSpinner.setOnItemSelectedListener(spinnerListener);

		return dialogView;
	}


	/**
	 * Initialize the custom dialog
	 */
	private void initializeCustomDialog() {
		myCustomAlertDialog = (MyCustomAlertDialog) findViewById(R.id.mycustomalertdialog);

		// Custom view
		myCustomAlertDialog.setView(initializeCustomAlertDialogView());

		// Changing the title
		myCustomAlertDialog.setTitle("Prueba de funcionamiento");

		// Changing onClickListener
		myCustomAlertDialog.setPositiveClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onPositiveButtonClickedCustomDialog();
			}
		});

		// Changing onClickListener
		myCustomAlertDialog.setNegativeClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetCustomAlertDialogView();
				myCustomAlertDialog.cancel();
			}
		});
	}

	/**
	 * Handle the event of the button create new controller (onClick)
	 */
	private void onPositiveButtonClickedCustomDialog() {
		String controllerNameString = newControllerEditText.getText().toString();
		String arduinoPinString = pinNumberEditText.getText().toString();

		// Check if the user completed all the fields
		if ((!Util.isEmptyString(controllerNameString)) && (!Util.isEmptyString(arduinoPinString))) {

			// Initialize variables
			int position = customGridLayout.getControllersCount();
			String dataType = dataTypeSpinner.getSelectedItem().toString();
			String pinType = pinTypeSpinner.getSelectedItem().toString();
			String controllerTypeString = controllerTypeSpinner.getSelectedItem().toString();
			int controllerType = matchControllerType(controllerTypeString, pinType);
			if (controllerType == -1) {
				Util.displayMessage(context, getString(R.string.err_invalidcontroller));
				return;
			}

			// Create new controller
			Controller controller = new Controller(
					controllerNameString,
					controllerType,
					dataType,
					pinType,
					arduinoPinString,
					position,
					panelId,
					0
			);

			// Set the controllerId
			int controllerId = (mySQLiteHelper.insertController(controller));
			controller.setId(controllerId);

			// Refresh view
			addControllerView(controller);

			// Dismiss the dialog
			resetCustomAlertDialogView();
			myCustomAlertDialog.cancel();
		} else {
			// Don't dismiss the dialog
			Util.displayMessage(context, getString(R.string.err_completeallfields));
		}
	}

	/**
	 * Resets custom Alert Dialog view to its default values
	 */
	private void resetCustomAlertDialogView() {
		newControllerEditText.setText("");
		pinNumberEditText.setText("");
		pinTypeSpinner.setSelection(0);
		dataTypeSpinner.setSelection(0);
	}


	/**
	 * Save the state of the controller
	 */
	private void saveControllersState() {
		ArrayList<ControllerView> controllers = controllerViewManager.getControllers();
		for (ControllerView controller : controllers) {
			int data = UserPreferencesManager.getInstance(context).getSaveControllerState() ? controller.getControllerData() : 0;
			mySQLiteHelper.updateControllerData(controller.getControllerId(), data);
		}
	}

	/**
	 * Return int with the type of controller given the name and type as string
	 *
	 * @param controllerTypeString type of the controller as string
	 * @param pinTypeString        type of pin as string (digital-analog)
	 *
	 * @return int
	 */
	private int matchControllerType(String controllerTypeString, String pinTypeString) {
		if (controllerTypeString.equals(getString(R.string.controllertype_led))) {
			if (pinTypeString.equalsIgnoreCase("digital"))
				return ArduinoUSBCommunicationManager.CONTROLLER_LED_DIGITAL;
			else
				return ArduinoUSBCommunicationManager.CONTROLLER_LED_ANALOG;
		}

		if (controllerTypeString.equals(getString(R.string.controllertype_servo)))
			return ArduinoUSBCommunicationManager.CONTROLLER_SERVO;

		if (controllerTypeString.equals(getString(R.string.controllertype_humidsensor)))
			return ArduinoUSBCommunicationManager.CONTROLLER_HUMIDITY_SENSOR;

		if (controllerTypeString.equals(getString(R.string.controllertype_tempsensor)))
			return ArduinoUSBCommunicationManager.CONTROLLER_TEMP_SENSOR;

		if (controllerTypeString.equals(getString(R.string.controllertype_generalcontroller)))
			return ArduinoUSBCommunicationManager.CONTROLLER_GENERIC;

		if (controllerTypeString.equals(getString(R.string.controllertype_lightsensor)))
			return ArduinoUSBCommunicationManager.CONTROLLER_LIGHT_SENSOR;

		return -1;
	}


	/**
	 * Creates a new Controller layout
	 */
	private void addControllerView(Controller controller) {
		int controllerId = controller.getId();
		String name = controller.getName();
		int controllerType = controller.getControllerType();
		String arduinoPin = controller.getPinNumber();
		String pinType = controller.getPinType();
		String dataType = controller.getDataType();
		int data = controller.getData();

		// TODO remove controllerViewManager
		ControllerView controllerView = controllerViewManager.createControllerView(controllerId, name, controllerType, arduinoPin, pinType, dataType, data);
		customGridLayout.addController(controllerView, connectionIsActive);
	}

	/**
	 * Start communication with Arduino
	 */
	public void startCommunication() {
		Log.d(TAG, "StartCommunication");
		if (connectionIsActive) {
			return;
		}
		ArduinoResponseCodes responseCode = arduinoCommunication.startCommunication();
		if (responseCode.getCode() <= 0 && !responseCode.equals(ArduinoResponseCodes.ERROR_NO_DEVICE)) {
			Util.displayError(context, responseCode.getDescription());
		} else if (responseCode.equals(ArduinoResponseCodes.ERROR_NO_DEVICE)) {
			Log.e(TAG, "No devices found");
		} else {
			Log.d(TAG, "StartCommunication : OK");
			// if (!connectionIsActive) setLoading(true);
		}
	}


	/**
	 * End communication with Arduino
	 */
	private void endCommunication() {
		Log.e(TAG, "endCommunication()");
		arduinoCommunication.endCommunication();
		// if (responseCode.getCode() <= 0) {
		// 	Util.displayError(context, responseCode.getDescription());
		// }
	}


	/**
	 * Sends command via Serial Port
	 */
	private void sendCommand(int controllerId, int controllerType, String arduinoPin, int pinType, int dataType, int data) {
		ArduinoResponseCodes responseCode = arduinoCommunication.sendCommand(controllerId, controllerType, arduinoPin, pinType, dataType, data);
		if (responseCode.getCode() <= 0) {
			Util.displayError(context, responseCode.getDescription());
		}
	}





	/* ARDUINO SERIAL LISTENER FUNCTIONS */

	@Override
	public void receivedData(final int panelId, final int controllerId, final String data, String units) {
		Log.d(TAG, "RECEIVEDDATA : Data received - " + controllerId + "-" + data);

		// Inform the the controller layout that data was received
		final String auxData = data;
		controllerViewManager.receivedData(panelId, controllerId, data, units);
	}

	@Override
	public void connectionOpened() {
		Log.d(TAG, "connectionOpened : OK");

		// Set connection active flag to true
		connectionIsActive = true;
		controllerViewManager.startControllers();
	}

	@Override
	public void connectionClosed(ArduinoResponseCodes responseCode) {
		// TODO IT2-3 delete all saved state values (like switches or sliders state)
		Log.d(TAG, "Connection closed");

		// Set connection active flag to false
		connectionIsActive = false;
		controllerViewManager.endControllers();
	}

	@Override
	public void connectionFailed(final ArduinoResponseCodes arduinoResponseCode) {
		Log.d(TAG, "connectionFailed");

		// Set connection active flag to false
		connectionIsActive = false;
		controllerViewManager.endControllers();

		// Display error on UI thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Util.displayError(context, arduinoResponseCode.getDescription());
			}
		});
	}


	/* CONTROLLER VIEW LISTENER FUNCTIONS */

	@Override
	public void controllerSentCommand(int controllerId, int controllerType, final String arduinoPin, final int pinType, final int dataType, final int data) {
		Log.d(TAG, "ControllerChangeState : Sending command to Arduino");
		sendCommand(controllerId, controllerType, arduinoPin, pinType, dataType, data);
	}

	@Override
	public void controllersPositionChanged(int initialPosition, int finalPosition) {
		mySQLiteHelper.updateIndexes(panelId, initialPosition, finalPosition);
	}

	@Override
	public void controllerRemoved(ControllerView controllerView) {
		Log.d(TAG, "Removing controller position : " + controllerView.getPosition());

		// Update indexes in database
		mySQLiteHelper.updateIndexes(panelId, controllerView.getPosition(), Integer.MAX_VALUE);

		// Remove controllerview from the layout
		customGridLayout.removeController(controllerView);

		// Remove controller from database
		mySQLiteHelper.deleteController(controllerView.getControllerId());
	}

	@Override
	public void controllerEditButtonPressed(ControllerView controllerView) {
		Log.d(TAG, "Updating controller in position : " + controllerView.getPosition());

		final ControllerView finalControllerView = controllerView;

		if (connectionIsActive) {
			finalControllerView.endController();
		}

		// Initialize variables
		View customView = getLayoutInflater().inflate(R.layout.alertdialog_editcontroller, null);
		final EditText etName = (EditText) customView.findViewById(R.id.et_editcontroller_name);
		final EditText etPin = (EditText) customView.findViewById(R.id.et_editcontroller_pin);

		// Fill editTexts with the actual values of the controller
		etName.setText(controllerView.getName());
		etPin.setText(controllerView.getArduinoPin());

		// Create alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Customize the builder
		alertDialogBuilder.setTitle("Edit controller")
				.setView(customView)
				.setPositiveButton("Aceptar", null)
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (connectionIsActive) {
							finalControllerView.startController();
						}
					}
				});

		// Create and show the dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

		// Override
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String newName = etName.getText().toString();
				String newPinNumber = etPin.getText().toString();

				// If empty fields display error
				if (Util.isEmptyString(newName) || Util.isEmptyString(newPinNumber)) {
					Toast.makeText(context, R.string.err_completeallfields, Toast.LENGTH_SHORT).show();
				} else {
					// Update controller in database
					mySQLiteHelper.updateController(
							new Controller(
									finalControllerView.getControllerId(),
									newName,
									finalControllerView.getControllerType(),
									String.valueOf(finalControllerView.getControllerData()),
									String.valueOf(finalControllerView.getPinType()),
									newPinNumber,
									finalControllerView.getPosition(),
									panelId,
									finalControllerView.getData()
							)
					);

					// Change the controller view
					finalControllerView.setName(newName);
					finalControllerView.setArduinoPin(newPinNumber);
					// controllerView.setArduinoPin(String.valueOf(newPinNumber));
					// controllerView.updatePinNumberTextView();

					alertDialog.dismiss();

					if (connectionIsActive) {
						finalControllerView.startController();
					}
				}
			}
		});


	}
}

















