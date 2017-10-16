package es.udc.psi1718.project;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.arduinomanager.ArduinoResponseCodes;
import es.udc.psi1718.project.arduinomanager.ArduinoSerialListener;
import es.udc.psi1718.project.ui.customviews.ControllerSwitchView;
import es.udc.psi1718.project.ui.customviews.ControllerViewEventListener;

public class ControllersActivity extends AppCompatActivity implements ArduinoSerialListener, ControllerViewEventListener {

	private Context context = this;
	private String TAG = "ControllersActivity";

	private final Boolean DEBUG = true;

	// Layout variables
	private FloatingActionButton fab;
	private Button buttonStartComm;
	private TextView loadingTextView;
	private ProgressBar progressBar;
	private LinearLayout mainLinearLayout;
	private RelativeLayout loadingLayout;
	private RelativeLayout normalLayout;

	// Arduino communication Manager
	private ArduinoCommunicationManager arduinoCommunication;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controllers);

		// TODO create a tutorial for first time opening

		Log.d(TAG, "ONCREATE");

		// Intents
		// TODO receive id of the pannel to retrieve previous controllers

		// Arduino communication
		arduinoCommunication = new ArduinoCommunicationManager(context);
		// TODO eliminar la necesidad de usar un BroadcastReceiver
		broadcastReceiver = arduinoCommunication.getBroadcastReceiver();

		// Initialize layout
		initializeLayout();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home)
			this.finish();
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "ONRESUME");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "ONDESTROY");
		endCommunication();
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(broadcastReceiver, arduinoCommunication.getBroadcastFilter());
		Log.d(TAG, "Receiver registered");
		Log.d(TAG, "ONSTART");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "ONRESTART");
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(broadcastReceiver);
		Log.d(TAG, "Receiver unregistered");
		Log.d(TAG, "ONSTOP");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "ONPAUSE");

	}


	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		// TODO load previous controllers in this function
		fab = (FloatingActionButton) findViewById(R.id.fab_new_controller);
		mainLinearLayout = (LinearLayout) findViewById(R.id.controllers_main_layout);
		loadingLayout = (RelativeLayout) findViewById(R.id.controllers_loading_layout);
		normalLayout = (RelativeLayout) findViewById(R.id.controllers_parent_layout);
		loadingTextView = (TextView) findViewById(R.id.controllers_loading_text_view);
		buttonStartComm = (Button) findViewById(R.id.controllers_start_comm_button);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		// Create one common listener
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.fab_new_controller:
						createNewControllerDialog();
						break;
					case R.id.controllers_start_comm_button:
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

		// TODO DEBUG remove this line when not debugging
		// Create new Controller for test purposes
		createNewController("Controller prueba", "8", "Digital", "Write");
//		ControllerSliderView controllerSwitchView = new ControllerSliderView(context, "Prueba", "3", "Analog", "Write");
//		mainLinearLayout.addView(controllerSwitchView.getView());

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
		/* TODO MATÍAS 3
		 * Esta es la función que se llama desde el 'AlertDialog' que tuviste que corregir antes en el paso 2.
		 *
		 * Comprobar lo siguiente : (puede que las clases aún no estén creadas, pero el constructor
		 * va a ser el mismo que el de 'ControllerSwitchView'. Deja la línea comentada si da error)
		 *     Si pinType == DIGITAL && dataType == WRITE ControllerView view = new ControllerSwitchView
		 *     Si pinType == ANALOG && dataType == WRITE ControllerView view = new ControllerSliderView
		 *     Si dataType == READ ControllerView view = new ControllerDataDisplayView
		 *
		 * Después de todos los ifs dejar escrita la línea de mainLinearLayout.addView(controllerSwitchView.getView());
		 */
		ControllerSwitchView controllerSwitchView = new ControllerSwitchView(context, name, arduinoPin, pinType, dataType);
		mainLinearLayout.addView(controllerSwitchView.getView());
	}


	/**
	 * Inflates an Alert Dialog to create a new controller
	 */
	private void createNewControllerDialog() {
		// Create AlertDialog builder
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		// Inflate and set the custom view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alert_dialog_new_controller_layout, null);
		dialogBuilder.setView(dialogView);

		// Save the views inside the alertDialog
		final EditText newControllerEditText = (EditText) dialogView.findViewById(R.id.new_controller_name_edit_text);
		final EditText pinNumberEditText = (EditText) dialogView.findViewById(R.id.new_controller_pin_number_edit_text);
		final Spinner pinTypeSpinner = (Spinner) dialogView.findViewById(R.id.new_controller_pin_type_spinner);
		final Spinner dataTypeSpinner = (Spinner) dialogView.findViewById(R.id.new_controller_data_type_spinner);

		// Set the rest of the options
		dialogBuilder
				.setCancelable(false)
				.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						String aux1 = newControllerEditText.getText().toString().replace(" ", "");
						String aux2 = pinNumberEditText.getText().toString().replace(" ", "");

						if (!(aux1.matches("") || aux2.matches(""))) {
							createNewController(
									newControllerEditText.getText().toString(),
									pinNumberEditText.getText().toString(),
									pinTypeSpinner.getSelectedItem().toString(),
									dataTypeSpinner.getSelectedItem().toString()
							);
						} // TODO Mirar como meter un dialogo de error
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});

		// Display AlertDialog
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
		Log.d(TAG, "createNewControllerDialog : created AlertDialog");
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
		String loadingText = loading ? "Loading..." : "Connect Arduino now";
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
	private void startCommunication() {
		Log.d(TAG, "StartCommunication");

		ArduinoResponseCodes responseCode = arduinoCommunication.startCommunication();
		if (responseCode.getCode() <= 0) {
			displayMessage("Ha habido un error : " + responseCode.getDescription());
		} else {
			Log.d(TAG, "StartCommunication : OK");
			setLoading(true);
		}
	}


	/**
	 * End communication with Arduino
	 */
	private void endCommunication() {
		ArduinoResponseCodes responseCode = arduinoCommunication.closeConnection();
		if (responseCode.getCode() <= 0) {
			displayMessage("Ha habido un error : " + responseCode.getDescription());
		}
	}


	/**
	 * Sends command via Serial Port
	 */
	private void sendCommand(int arduinoPin, int pinType, int dataType, int data) {
		ArduinoResponseCodes responseCode = arduinoCommunication.sendCommand(arduinoPin, pinType, dataType, data);
		if (responseCode.getCode() > 0) {
			// TODO decidir que hacer después de mandar un mensaje
		} else {
			displayMessage("Ha habido un error : " + responseCode.getDescription());
		}
	}


	/**
	 * Displays a message on UI thread
	 *
	 * @param message
	 */
	private void displayMessage(final String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}



	/* ARDUINO SERIAL LISTENER FUNCTIONS */

	@Override
	public void receivedData(String data) {
		Log.d(TAG, "RECEIVEDDATA : Data received - " + data);
		displayMessage(data);
	}

	@Override
	public void connectionOpened() {
		Log.d(TAG, "connectionOpened : OK");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO comprobar si se ha cancelado la comunicación
				enableUI();
				displayMessage("Connection opened!");
			}
		});
	}

	@Override
	public void connectionClosed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableUI();
				displayMessage("Connection closed");
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
				displayMessage(arduinoResponseCode.getDescription());
				setLoading(false);
			}
		});
	}


	/* CONTROLLER VIEW LISTENER FUNCTIONS */

	@Override
	public void controllerChangedState(final int arduinoPin, final int pinType, final int dataType, final int data) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "ControllerChangeState : Sending command to Arduino");
				sendCommand(arduinoPin, pinType, dataType, data);
			}
		});
	}
}
