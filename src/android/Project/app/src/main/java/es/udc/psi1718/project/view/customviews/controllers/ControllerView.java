package es.udc.psi1718.project.view.customviews.controllers;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public abstract class ControllerView extends LinearLayout {

	private final String TAG = "ControllerView";

	ControllerViewEventListener listener;

	// Command variables
	private String arduinoPin;
	private int pinType;
	private int commandType;
	private int controllerType;

	// Layout variables
	private String name;

	public ControllerView(Context context, String name, int controllerType, String arduinoPin, int pinType, int commandType) {
		super(context);
		this.name = name;
		this.controllerType = controllerType;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.commandType = commandType;
		listener = (ControllerViewEventListener) context;
	}


	/**
	 * Allows the user to modify controller's data
	 */
	void editController() {
		// TODO IT2 display alertDialog to modify the values
		Log.d(TAG, "Modify controller");
		setName("Controller modificado");
	}


	/**
	 * Sends command via 'ControllerViewEventListener'
	 *
	 * @param data int value to write to Arduino
	 */
	void sendCommand(int data) {
		// Send command via interface
		listener.controllerSentCommand(controllerType, arduinoPin, pinType, commandType, data);
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
