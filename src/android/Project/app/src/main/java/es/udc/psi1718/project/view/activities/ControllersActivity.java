package es.udc.psi1718.project.view.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import es.udc.psi1718.project.MyBroadcastReceiver;
import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.arduinomanager.ArduinoResponseCodes;
import es.udc.psi1718.project.arduinomanager.ArduinoSerialConnectionListener;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Controller;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.controllers.ControllerView;
import es.udc.psi1718.project.view.customviews.controllers.ControllerViewEventListener;
import es.udc.psi1718.project.view.customviews.controllers.ControllerViewManager;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridLayout;
import es.udc.psi1718.project.view.customviews.controllersgrid.ControllersGridListener;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

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

	// Custom Alert Dialog
	private DisplayMetrics mDisplayMetrics;
	private Float initialFabX, initialFabY;
	private int finalFabX, finalFabY = 0;
	private final Float fadeAlpha = 0.8f;
	private boolean isCustomAlertDialogOpened = false;
	private Button btnCreate, btnCancel;
	private LinearLayout fadeLayout;
	private LinearLayout customAlertLayout;
	private String[] controllersType;
	private ControllerViewManager controllerViewManager;

	// Layout variables
	private FloatingActionButton fabNewController;
	private Button buttonStartComm;
	private TextView loadingTextView;
	private ProgressBar progressBar;
	private ControllersGridLayout customGridLayout;
	private RelativeLayout loadingLayout;
	private RelativeLayout normalLayout;
	private EditText newControllerEditText, pinNumberEditText;
	private Spinner pinTypeSpinner, dataTypeSpinner;
	private Spinner controllerTypeSpinner;

	// Arduino communication Manager
	private ArduinoCommunicationManager arduinoCommunication;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;
	private Boolean connectionIsActive = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (connectionIsActive || Constants.DEBUG) {
			setContentView(R.layout.activity_controllers);
			initializeLayout();
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

		// Set active flag to true
		active = true;

		// Register USB receiver
		Log.e(TAG, "onStart : REGISTER RECEIVER USB");
		registerReceiver(broadcastReceiver, intentFilter);

		// Start communication
		if (!connectionIsActive) startCommunication();

		// Add animation to the fabNewController
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_grow_anim);
		fabNewController.startAnimation(animation);
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

		// Unregister broadcast receiver
		try {
			unregisterReceiver(broadcastReceiver);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		// End communication with Arduino
		endCommunication();

		// setResult(RESULT_OK, null);
		// finish();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Log.e(TAG, "onBackPressed");
		if (isCustomAlertDialogOpened) closeCustomAlertDialog();
		else supportFinishAfterTransition();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
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
			createNewController(
					controller.getId(),
					controller.getName(),
					controller.getControllerType(),
					controller.getPinNumber(),
					controller.getPinType(),
					controller.getDataType());
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
		fabNewController = (FloatingActionButton) findViewById(R.id.fab_new_controller);
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
						// createNewControllerDialog();
						openMyCustomAlertDialog();
						break;
					case R.id.controllers_retry_button:
						// TODO DEBUG uncomment this when not debugging
						if (!Constants.DEBUG) {
							startCommunication();
						}
						break;
					default:
						break;
				}
			}
		};

		// Add listener to different layout items
		fabNewController.setOnClickListener(onClickListener);
		buttonStartComm.setOnClickListener(onClickListener);

		// By default, layout is disabled
		if (connectionIsActive || Constants.DEBUG) {
			enableUI();
		} else {
			disableUI();
			setLoading(false);
		}

		// Restore previous Controllers
		loadSavedControllers();

		// Initialize custom alert dialog layout
		initializeCustomAlertDialogLayout();
	}


	/**
	 * Initialize all variables needed for custom alert dialog
	 */
	private void initializeCustomAlertDialogLayout() {
		controllersType = new String[]{
				getString(R.string.controllertype_led),
				getString(R.string.controllertype_servo),
				getString(R.string.controllertype_tempsensor),
				getString(R.string.controllertype_humidsensor),
				getString(R.string.controllertype_generalcontroller)};

		mDisplayMetrics = getResources().getDisplayMetrics();
		fadeLayout = (LinearLayout) findViewById(R.id.customalertdialog_layout_fade);
		customAlertLayout = (LinearLayout) findViewById(R.id.customalertdialog_layout_newpanel);
		btnCancel = (Button) findViewById(R.id.btn_customalertdialog_cancel);
		btnCreate = (Button) findViewById(R.id.btn_customalertdialog_create);
		newControllerEditText = (EditText) findViewById(R.id.et_newcontroller_name);
		pinNumberEditText = (EditText) findViewById(R.id.et_newcontroller_pinnumber);
		controllerTypeSpinner = (Spinner) findViewById(R.id.spinner_newcontroller_type);
		pinTypeSpinner = (Spinner) findViewById(R.id.spinner_newcontroller_pintype);
		dataTypeSpinner = (Spinner) findViewById(R.id.spinner_newcontroller_datatype);
		final LinearLayout layoutPinType = (LinearLayout) findViewById(R.id.layout_newcontroller_pintype);
		final LinearLayout layoutDataType = (LinearLayout) findViewById(R.id.layout_newcontroller_datatype);

		// Create one common listener for the buttons
		View.OnClickListener buttonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.btn_customalertdialog_cancel:
						closeCustomAlertDialog();
						break;
					case R.id.btn_customalertdialog_create:
						handleCreateNewControllerButton();
						break;
					default:
						break;
				}
			}
		};

		// Add listener to the buttons
		btnCancel.setOnClickListener(buttonClickListener);
		btnCreate.setOnClickListener(buttonClickListener);

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
	}


	/**
	 * Handle the event of the button create new controller (onClick)
	 */
	private void handleCreateNewControllerButton() {
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
					panelId
			);
			// TODO save this value and use it in createNewController()
			int controllerId = (mySQLiteHelper.insertController(controller));

			// Refresh view
			createNewController(
					controllerId,
					controllerNameString,
					controllerType,
					arduinoPinString,
					pinType,
					dataType);

			// Dismiss the dialog
			closeCustomAlertDialog();
		} else {
			// Don't dismiss the dialog
			Util.displayMessage(context, getString(R.string.err_completeallfields));
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
				return ArduinoCommunicationManager.CONTROLLER_LED_DIGITAL;
			else
				return ArduinoCommunicationManager.CONTROLLER_LED_ANALOG;
		}

		if (controllerTypeString.equals(getString(R.string.controllertype_servo)))
			return ArduinoCommunicationManager.CONTROLLER_SERVO;

		if (controllerTypeString.equals(getString(R.string.controllertype_humidsensor)))
			return ArduinoCommunicationManager.CONTROLLER_HUMIDITY_SENSOR;

		if (controllerTypeString.equals(getString(R.string.controllertype_tempsensor)))
			return ArduinoCommunicationManager.CONTROLLER_TEMP_SENSOR;

		if (controllerTypeString.equals(getString(R.string.controllertype_generalcontroller)))
			return ArduinoCommunicationManager.CONTROLLER_GENERIC;

		return -1;
	}


	/**
	 * Creates a new Controller layout
	 */
	private void createNewController(int controllerId, String name, int controllerType, String arduinoPin, String pinType, String dataType) {
		ControllerView controllerView = controllerViewManager.createControllerView(controllerId, name, controllerType, arduinoPin, pinType, dataType);
		customGridLayout.addController(controllerView);
	}

	/**
	 * Shows a custom alert dialog for creating a new controller
	 */
	private void openMyCustomAlertDialog() {
		// Initialize measurement variables
		if (finalFabY == 0) {
			initialFabX = fabNewController.getX();
			initialFabY = fabNewController.getY();
			finalFabX = (mDisplayMetrics.widthPixels / 2) - fabNewController.getWidth() / 2;
			Log.e(TAG, "Value is : " + fabNewController.getWidth());
			Log.e(TAG, "Screen is : " + mDisplayMetrics.widthPixels);
			finalFabY = (int) (initialFabY - fabNewController.getHeight() * 2);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// Move fabNewController to the new position
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Move fabNewController to the new position
					fabNewController.animate()
							.x(finalFabX)
							.y(finalFabY)
							.setDuration(300)
							.start();
				}
			}, 50);

			// Inflate the new layout and add fade to the screen
			new Handler().postDelayed(new Runnable() {
				@SuppressLint("newApi")
				@Override
				public void run() {
					if (!isCustomAlertDialogOpened) return;
					// Add fade to the background
					fadeLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							closeCustomAlertDialog();
						}
					});
					fadeLayout.setClickable(true);
					fadeLayout.setFocusable(true);
					fadeLayout.setAlpha(0f);
					fadeLayout.setVisibility(View.VISIBLE);
					fadeLayout.animate()
							.alpha(fadeAlpha)
							.setDuration(300)
							.start();

					// Set visibility of the whole panel to VISIBLE
					customAlertLayout.setVisibility(View.VISIBLE);

					// If API > 21 animate with a circular material transition

					Animator a = ViewAnimationUtils.createCircularReveal(
							customAlertLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - customAlertLayout.getY()) + fabNewController.getSize() / 2,
							fabNewController.getSize() / 2,
							customAlertLayout.getHeight() * 2f);

					a.start();
				}
			}, 350);
		} else {
			// Add fade to the background
			fadeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					closeCustomAlertDialog();
				}
			});
			fadeLayout.setClickable(true);
			fadeLayout.setFocusable(true);
			fadeLayout.setAlpha(0f);
			fadeLayout.setVisibility(View.VISIBLE);
			fadeLayout.animate()
					.alpha(fadeAlpha)
					.setDuration(300)
					.start();
			// Show the main layout
			customAlertLayout.setVisibility(View.VISIBLE);
		}

		isCustomAlertDialogOpened = true;
	}


	/**
	 * Closes the custom alertDialog
	 */
	private void closeCustomAlertDialog() {
		// Hide the keyboard
		Util.hideKeyboard(activity);

		// Hide layout and fade
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Remove fade from the background
				fadeLayout.setClickable(false);
				fadeLayout.setFocusable(false);
				fadeLayout.animate()
						.alpha(0f)
						.setDuration(300)
						.start();

				// Set visibility of the whole panel to INVISIBLE
				// customAlertLayout.setVisibility(View.VISIBLE);

				// If API > 21 animate with a circular material transition
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Animator a = ViewAnimationUtils.createCircularReveal(
							customAlertLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - customAlertLayout.getY()) + fabNewController.getSize() / 2,
							customAlertLayout.getHeight() * 2f,
							fabNewController.getSize() / 2);
					a.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							customAlertLayout.setVisibility(View.INVISIBLE);
						}
					});

					a.start();
				} else {
					customAlertLayout.setVisibility(View.INVISIBLE);
				}
			}
		}, 50);

		// Move fab back to its position
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Move fab to the new position
				fabNewController.animate()
						.x(initialFabX)
						.y(initialFabY)
						.setDuration(300)
						.start();
			}
		}, 350);

		// Clear the layout
		newControllerEditText.setText("");
		pinNumberEditText.setText("");
		pinTypeSpinner.setSelection(0);
		dataTypeSpinner.setSelection(0);
		isCustomAlertDialogOpened = false;
	}


	/**
	 * Display loading layout
	 *
	 * @param loading Boolean to indicate whether it's loading or not
	 */
	private void setLoading(Boolean loading) {
		Log.e(TAG, "SET LOADING : " + loading);
		String loadingText = loading ? "Loading..." : "No devices found";
		int progressBarVisibility = loading ? View.VISIBLE : View.GONE;

		buttonStartComm.setEnabled(!loading);
		loadingTextView.setText(loadingText);
		progressBar.setVisibility(progressBarVisibility);
	}


	/**
	 * Disables UI, user can only start communication with arduino
	 */
	private void enableUI() {
		Log.e(TAG, "ENABLE UI");
		loadingLayout.setVisibility(View.GONE);
		normalLayout.setVisibility(View.VISIBLE);
		setLoading(false);
	}


	/**
	 * Enables UI so that the user can interact with the application
	 */
	private void disableUI() {
		Log.e(TAG, "DISABLE UI");
		loadingLayout.setVisibility(View.VISIBLE);
		normalLayout.setVisibility(View.GONE);
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
			if (!connectionIsActive) setLoading(true);
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
	public void receivedData(final int panelId, final int controllerId, final String data) {
		Log.d(TAG, "RECEIVEDDATA : Data received - " + controllerId + "-" + data);
		final String auxData = data;
		// controllerViewManager.receivedData(panelId, controllerId, data);

		// TODO move 'runOnUiThread' to the controller view itself, executing this in background
		controllerViewManager.receivedData(panelId, controllerId, data);
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

		// Set connection active flag to false
		connectionIsActive = true;
		controllerViewManager.startControllers();

		// Change the layout running on UI thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO IT2 comprobar si se ha cancelado la comunicación
				Log.e(TAG, "Connection is OPENED");
				enableUI();
			}
		});
	}

	@Override
	public void connectionClosed(ArduinoResponseCodes responseCode) {
		// TODO IT2-3 delete all saved state values (like switches or sliders state)
		Log.d(TAG, "Connection closed");

		// Set connection active flag to false
		connectionIsActive = false;
		controllerViewManager.endControllers();

		// Change the layout running on UI thread
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
		Log.d(TAG, "Connection failed");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableUI();
				Util.displayError(context, arduinoResponseCode.getDescription());
				setLoading(false);
				connectionIsActive = false;
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
}