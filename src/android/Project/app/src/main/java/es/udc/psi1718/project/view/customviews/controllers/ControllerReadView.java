package es.udc.psi1718.project.view.customviews.controllers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;

public class ControllerReadView extends ControllerView {
	private String TAG = "ControllerSwitchView";

	private View view;
	private String name;
	private TextView nameTextView;


	public ControllerReadView(Context context, String name, int controllerType, String arduinoPin) {
		super(context, name, controllerType, arduinoPin, ArduinoCommunicationManager.PINTYPE_DIGITAL, ArduinoCommunicationManager.COMMANDTYPE_WRITE);
		initializeLayout(name, arduinoPin);
	}

	private void initializeLayout(String name, String arduinoPin) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_read_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		TextView tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		TextView tvData = (TextView) view.findViewById(R.id.tv_controller_data);

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
