package es.udc.psi1718.project.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.view.customviews.alertdialogs.MyCustomAlertDialog;

public class TestActivity extends AppCompatActivity {
	private Context context = this;
	private final static String TAG = "TestActivity";
	private MyCustomAlertDialog myCustomAlertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_test);

		initializeCustomDialog();
	}

	private void initializeCustomDialog() {
		myCustomAlertDialog = (MyCustomAlertDialog) findViewById(R.id.mycustomalertdialog);

		// Custom view
		myCustomAlertDialog.setView(initializeView());

		// Changing the title
		myCustomAlertDialog.setTitle("Prueba de funcionamiento");

		// Changing onClickListener
		myCustomAlertDialog.setPositiveClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "Creado controlador", Toast.LENGTH_SHORT).show();
				myCustomAlertDialog.cancel();
			}
		});

		// Changing onClickListener
		myCustomAlertDialog.setNegativeClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "cancelador wei", Toast.LENGTH_SHORT).show();
				myCustomAlertDialog.cancel();

			}
		});
	}

	private View initializeView() {
		// Inflate the new view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newcontroller_layout, null);

		final String[] controllersType = new String[]{
				getString(R.string.controllertype_led),
				getString(R.string.controllertype_servo),
				getString(R.string.controllertype_tempsensor),
				getString(R.string.controllertype_humidsensor),
				getString(R.string.controllertype_generalcontroller)};

		EditText newControllerEditText = (EditText) dialogView.findViewById(R.id.et_newcontroller_name);
		EditText pinNumberEditText = (EditText) dialogView.findViewById(R.id.et_newcontroller_pinnumber);
		Spinner controllerTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_type);
		Spinner pinTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_pintype);
		Spinner dataTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_newcontroller_datatype);
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
}
