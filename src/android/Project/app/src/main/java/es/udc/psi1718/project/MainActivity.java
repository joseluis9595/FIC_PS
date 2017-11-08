package es.udc.psi1718.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.udc.psi1718.project.util.Constants;

public class MainActivity extends AppCompatActivity {

	private Context context = this;
	private String TAG = "MainActivity";

	// Intents
	private Intent controllersIntent;

	// Layout variables
	//private Button buttonSendCommand;
	private Button buttonStartComm;
	//private TextView textView;
	//private EditText editText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");

		// TODO IT2 implement the whole class

		// Intents
		controllersIntent = new Intent(context, ControllersActivity.class);


		// Initialize layout
		//initializeLayout();
	}


	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		// TODO IT2 remove this implementation
		// Don't allow to start this activity yet
		// startActivity(controllersIntent);
		startActivityForResult(controllersIntent, Constants.ACTIVITYRESULT_REQUESTEXIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO IT2 remove this function
		if (requestCode == Constants.ACTIVITYRESULT_REQUESTEXIT) {
			if (resultCode == RESULT_OK) {
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}


	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		//buttonSendCommand = (Button) findViewById(R.id.button_send_command);
		buttonStartComm = (Button) findViewById(R.id.button_start_comm);
		// TODO DEBUG eliminar este texto
		buttonStartComm.setText("Abrir panel 1 (provisional)");
		//editText = (EditText) findViewById(R.id.editText);
		//textView = (TextView) findViewById(R.id.textView);

		// Create one common listener
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.button_start_comm:
						Log.d(TAG, "ONCLICK : button start");
						// TODO DEBUG recuperar la función original startCommunication();
						startActivity(controllersIntent);
						break;

					/*case R.id.button_send_command:
						Log.d(TAG, "ONCLICK : button send - " + editText.getText().toString());
						//sendCommand(editText.getText().toString());
						break;*/

					default:
						Log.e(TAG, "ONCLICK : Pressed a non-listened layout");
				}
			}
		};

		// Add listener to different layout items
		//buttonSendCommand.setOnClickListener(onClickListener);
		buttonStartComm.setOnClickListener(onClickListener);


	}


}
