package es.udc.psi1718.project.view.customviews.controllers;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


public abstract class ControllerView extends LinearLayout {

	private final String TAG = "ControllerView";

	ControllerViewEventListener listener;

	Activity fromContext;

	// Command variables
	private int controllerId;
	private String arduinoPin;
	private int pinType;
	private int commandType;
	private int controllerType;

	// Layout variables
	private String name;

	public ControllerView(Activity context, int controllerId, String name, int controllerType, String arduinoPin, int pinType, int commandType) {
		super(context);
		this.fromContext = context;
		this.controllerId = controllerId;
		this.name = name;
		this.controllerType = controllerType;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.commandType = commandType;
		listener = (ControllerViewEventListener) context;
	}

	/**
	 * Called when data is received
	 *
	 * @param panelId      id of the panel
	 * @param controllerId id of the controller
	 * @param data         data received
	 */
	public void receivedData(int panelId, int controllerId, String data) {
		if (controllerId == this.controllerId) {
			Log.e(TAG, "Received data on controller ID " + controllerId + " : " + data);
			refreshController(data);
		}

	}

	/**
	 * Called when data is received
	 *
	 * @param data data received
	 */
	abstract void refreshController(String data);


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
		listener.controllerSentCommand(controllerId, controllerType, arduinoPin, pinType, commandType, data);
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

	public abstract void startController();

	public abstract void endController();

}
