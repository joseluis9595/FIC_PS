package es.udc.psi1718.project.view.customviews.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;


public class ControllerDigitalWriteView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	private View view;
	private TextView nameTextView, tvPinNumber;
	private Switch mSwitch;
	private LinearLayout cardViewLayout;


	public ControllerDigitalWriteView(Context context, String name, int controllerType, String arduinoPin) {
		super(context, name, controllerType, arduinoPin, ArduinoCommunicationManager.PINTYPE_DIGITAL, ArduinoCommunicationManager.COMMANDTYPE_WRITE);
		initializeLayout(name, arduinoPin);
	}


	private void initializeLayout(String name, String arduinoPin) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_switch_layout, null);

		// Initialize variables
		cardViewLayout = (LinearLayout) view.findViewById(R.id.card_view_main_layout);
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);

		mSwitch = (Switch) view.findViewById(R.id.controller_switch);

		// Create listeners
		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				int data = (b) ? 1 : 0;
				Log.d(TAG, "OnCheckedChange()");
				ControllerDigitalWriteView.super.sendCommand(data);
			}
		};

		// OnLongClickListener onLongClickListener = new OnLongClickListener() {
		// 	@Override
		// 	public boolean onLongClick(View view) {
		// 		switch (view.getId()) {
		// 			case R.id.card_view_main_layout:
		// 				// TODO IT2 edit cardView
		// 				ControllerSwitchView.super.editController();
		// 				return true;
		// 			default:
		// 				break;
		// 		}
		// 		return false;
		// 	}
		// };
		//
		// // Modify layout
		// cardViewLayout.setOnLongClickListener(onLongClickListener);
		mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
		nameTextView.setText(name);
		tvPinNumber.setText("Pin : " + arduinoPin);
	}


	@Override
	public View getView() {
		return view;
	}

	@Override
	public void setName(String newName) {
		nameTextView.setText(newName);
	}
	
}
