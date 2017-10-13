package es.udc.psi1718.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ControllersActivity extends AppCompatActivity {

	private Context context = this;
	private String TAG = "ControllersActivity";

	// Layout variables
	private FloatingActionButton fab;
	private LinearLayout mainLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controllers);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Intents

		// Other initializations

		// Initialize layout
		initializeLayout();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home)
			this.finish();
		return true;
	}


	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		fab = (FloatingActionButton) findViewById(R.id.fab_new_controller);
		mainLinearLayout = (LinearLayout) findViewById(R.id.controllers_main_layout);

		// Create one common listener
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.fab_new_controller:
						createNewControllerDialog();
						break;
					default:
						break;
				}
			}
		};

		// Add listener to different layout items
		fab.setOnClickListener(onClickListener);
	}


	/**
	 * Creates a new Controller layout
	 */
	private void createNewController(String name, String arduinoPin, String pinType, String dataType) {
		// TODO create new Controller
		//View controllerView = this.getLayoutInflater().inflate(R.layout.layout_controller, null);
		//mainLinearLayout.addView(controllerView);
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
						// TODO añadir CardView al linearLayout con los datos introducidos
						createNewController(
								newControllerEditText.getText().toString(),
								pinNumberEditText.getText().toString(),
								pinTypeSpinner.getSelectedItem().toString(),
								dataTypeSpinner.getSelectedItem().toString()
						);
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

}
