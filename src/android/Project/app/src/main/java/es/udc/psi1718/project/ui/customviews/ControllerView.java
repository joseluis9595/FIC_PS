package es.udc.psi1718.project.ui.customviews;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;

public abstract class ControllerView extends LinearLayout {

	private final String TAG = "ControllerView";

	ControllerViewEventListener listener;

	// Command variables
	private String name;
	private String arduinoPin;
	private String pinType;
	private String dataType;

	public ControllerView(Context context, String name, String arduinoPin, String pinType, String dataType) {
		super(context);
		this.name = name;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.dataType = dataType;
		listener = (ControllerViewEventListener) context;
	}


	void sendCommand(int data) {

		// Convert variables to integers
		int arduinoPintInt = Integer.parseInt(arduinoPin);

		int pinTypeInt;
		if (pinType.equalsIgnoreCase("DIGITAL")) {
			Log.d(TAG, "DIGITAL equals " + pinType);
			pinTypeInt = ArduinoCommunicationManager.PINTYPE_DIGITAL;
		} else {
			Log.d(TAG, "ANALOG equals " + pinType);
			pinTypeInt = ArduinoCommunicationManager.PINTYPE_ANALOG;
		}

		int dataTypeInt;
		if (dataType.equalsIgnoreCase("READ")) {
			dataTypeInt = ArduinoCommunicationManager.DATATYPE_READ;
		} else {
			dataTypeInt = ArduinoCommunicationManager.DATATYPE_WRITE;
		}

		Log.d(TAG, "Comprobación tipos de dato : ");
		Log.d(TAG, "PinType : " + pinType);
		Log.d(TAG, "DataType : " + dataType);

		// Send command via interface
		listener.controllerChangedState(arduinoPintInt, pinTypeInt, dataTypeInt, data);

	}


	/**
	 * Gets the view of the Controller
	 *
	 * @return View
	 */

	public abstract View getView();

	/**
	 * Changes the main Text view in the Controller View
	 *
	 * @param newName the new name you want for the Controller
	 */
	public abstract void setNameTextView(String newName);
}
