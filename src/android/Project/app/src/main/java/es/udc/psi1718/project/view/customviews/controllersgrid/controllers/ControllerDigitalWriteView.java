package es.udc.psi1718.project.view.customviews.controllersgrid.controllers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;


public class ControllerDigitalWriteView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	private View view;
	private TextView nameTextView;
	private ImageButton btnEdit;
	private Switch mSwitch;


	public ControllerDigitalWriteView(ControllerViewManager manager, Activity context, int controllerId,
									  String name, int controllerType, String arduinoPin, int data) {
		super(manager, context, controllerId, name, controllerType, arduinoPin,
				ArduinoCommunicationManager.PINTYPE_DIGITAL,
				ArduinoCommunicationManager.COMMANDTYPE_WRITE, data);
		initializeLayout(name, arduinoPin, data);
	}


	private void initializeLayout(String name, String arduinoPin, int data) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_switch_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		TextView tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		 mSwitch = (Switch) view.findViewById(R.id.controller_switch);
		btnEdit = (ImageButton) view.findViewById(R.id.btn_controllerview_edit);

		// Add click listener to the 3-dotted button
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				editController(btnEdit);
			}
		});

		// Create listeners
		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				int data = (b) ? 1 : 0;
				Log.d(TAG, "OnCheckedChange()");
				sendCommand(data);
			}
		};

		// Modify layout
		mSwitch.setChecked(data==1);
		mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
		nameTextView.setText(name);
		tvPinNumber.setText("Pin : " + arduinoPin);
	}

	@Override
	void refreshController(String data) {
		final String finalData = data;
		fromContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateNameTextView(finalData);
			}
		});
	}

	@Override
	public int getControllerData() {
		return mSwitch.isChecked()?1:0;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void updateNameTextView(String newName) {
		nameTextView.setText(newName);
	}

	@Override
	public void startController() {

	}

	@Override
	public void endController() {

	}

}
