package es.udc.psi1718.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.arduinomanager.ArduinoSerialListener;

public class MainActivity extends AppCompatActivity implements ArduinoSerialListener {

	private Context context = this;
	private String TAG = "MainActivity";

	// Layout variables
	private Button buttonSendCommand;
	private Button buttonStartComm;
	private TextView textView;
	private EditText editText;

	// Arduino communication Manager
	private ArduinoCommunicationManager arduinoCommunication;
	private BroadcastReceiver broadcastReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d(TAG, "ONCREATE");

		arduinoCommunication = new ArduinoCommunicationManager(this);
		// TODO eliminar la necesidad de usar un BroadcastReceiver
		broadcastReceiver = arduinoCommunication.getBroadcastReceiver();

		initializeLayout();
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
		buttonSendCommand = (Button) findViewById(R.id.button_send_command);
		buttonStartComm = (Button) findViewById(R.id.button_start_comm);
		editText = (EditText) findViewById(R.id.editText);
		textView = (TextView) findViewById(R.id.textView);

		// Create one common listener
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.button_start_comm:
						Log.d(TAG, "ONCLICK : button start");
						startCommunication();
						//Intent intent = new Intent(context, ControllersActivity.class);
						//startActivity(intent);
						break;

					case R.id.button_send_command:
						Log.d(TAG, "ONCLICK : button send - " + editText.getText().toString());
						sendCommand(editText.getText().toString());
						break;

					default:
						Log.e(TAG, "ONCLICK : Pressed a non-listened layout");
				}
			}
		};

		// Add listener to different layout items
		buttonSendCommand.setOnClickListener(onClickListener);
		buttonStartComm.setOnClickListener(onClickListener);

		// By default, layout is disabled
		disableUI();
	}

	private void enableUI() {
		buttonStartComm.setEnabled(false);
		buttonSendCommand.setEnabled(true);
		editText.setEnabled(true);
	}

	private void disableUI() {
		buttonStartComm.setEnabled(true);
		buttonSendCommand.setEnabled(false);
		editText.setEnabled(false);
	}


	private void startCommunication() {
		// TODO handle error codes (show toast with error information)
		int responseCode = arduinoCommunication.startCommunication();
		if (responseCode <= 0) {
			Toast.makeText(context, "Ha habido un error : " + responseCode, Toast.LENGTH_SHORT).show();
		}
	}


	private void endCommunication() {
		int responseCode = arduinoCommunication.closeConnection();
		if (responseCode <= 0) {
			Toast.makeText(context, "No hay ninguna conexión abierta", Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * Sends command via Serial Port
	 *
	 * @param command
	 */
	private void sendCommand(String command) {
		int responseCode = arduinoCommunication.sendCommand(command);
		if (responseCode > 0) {
			textView.append(command + "\n");
		} else {
			textView.append("Error " + responseCode + "\n");
		}

	}

	private void appendTextView(String data) {
		final TextView ftv = textView;
		final CharSequence ftext = data;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ftv.append(ftext);
			}
		});
	}

	@Override
	public void receivedData(String data) {
		Log.d(TAG, "RECEIVEDDATA : Data received - " + data);
		appendTextView(data);
	}

	@Override
	public void connectionOpened() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				enableUI();
				appendTextView("Connection opened!\n");
			}
		});
	}

	@Override
	public void connectionClosed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disableUI();
				appendTextView("Connection closed\n");
			}
		});
	}
}
