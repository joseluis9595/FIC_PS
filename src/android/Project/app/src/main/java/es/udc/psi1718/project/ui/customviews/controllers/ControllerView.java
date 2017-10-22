package es.udc.psi1718.project.ui.customviews.controllers;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public abstract class ControllerView extends LinearLayout {

	private final String TAG = "ControllerView";

	ControllerViewEventListener listener;

	// Command variables
	private String arduinoPin;
	private String pinType;
	private String commandType;

	// Layout variables
	private String name;

	public ControllerView(Context context, String name, String arduinoPin, String pinType, String commandType) {
		super(context);
		this.name = name;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.commandType = commandType;
		listener = (ControllerViewEventListener) context;

	}


	/**
	 * Allows the user to modify controller's data
	 */
	void editController() {
		// TODO IT4 display alertDialog to modify the values
		setName("Modificar Controller");
	}


	/**
	 * Sends command via 'ControllerViewEventListener'
	 *
	 * @param data int value to write to Arduino
	 */
	void sendCommand(int data, int pinType, int commandType) {
		// Convert variables to integers
		// TODO IT1 check if its properly formed
		// int arduinoPintInt = Integer.parseInt(arduinoPin);

		// int pinTypeInt;
		// if (pinType.equalsIgnoreCase("DIGITAL")) {
		// 	Log.d(TAG, "DIGITAL equals " + pinType);
		// 	pinTypeInt = ArduinoCommunicationManager.PINTYPE_DIGITAL;
		// } else {
		// 	Log.d(TAG, "ANALOG equals " + pinType);
		// 	pinTypeInt = ArduinoCommunicationManager.PINTYPE_ANALOG;
		// }
		//
		// int dataTypeInt;
		// if (dataType.equalsIgnoreCase("READ")) {
		// 	dataTypeInt = ArduinoCommunicationManager.COMMANDTYPE_READ;
		// } else {
		// 	dataTypeInt = ArduinoCommunicationManager.COMMANDTYPE_WRITE;
		// }

		// Log.d(TAG, "Comprobación tipos de dato : ");
		// Log.d(TAG, "PinType : " + pinType);
		// Log.d(TAG, "DataType : " + commandType);

		// Send command via interface
		listener.controllerSentCommand(arduinoPin, pinType, commandType, data);
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
	public abstract void setName(String newName);
}
