package es.udc.psi1718.project.view.customviews.controllersgrid.controllers;


import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import es.udc.psi1718.project.R;


public abstract class ControllerView extends LinearLayout {

	private final String TAG = "ControllerView";
	private ControllerView thisController = this;

	// Controller View Manager
	ControllerViewManager controllerViewManager;

	// Context
	Activity fromContext;

	// Controller variables
	private int controllerId;
	private String arduinoPin;
	private int pinType;
	private int commandType;
	private int controllerType;
	private String name;
	private int data;
	private int position;

	public ControllerView(ControllerViewManager manager, Activity context, int controllerId,
						  String name, int controllerType, String arduinoPin, int pinType, int commandType, int data) {
		super(context);
		this.fromContext = context;
		this.controllerId = controllerId;
		this.name = name;
		this.controllerType = controllerType;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.commandType = commandType;
		this.data = data;
		controllerViewManager = manager;
	}

	/**
	 * Called when data is received
	 *
	 * @param panelId      id of the panel
	 * @param controllerId id of the controller
	 * @param data         data received
	 */
	public void receivedData(int panelId, int controllerId, String data, String units) {
		if (controllerId == this.controllerId) {
			Log.e(TAG, "Received data on controller ID " + controllerId + " : " + data);
			refreshController(data, units);
		}

	}

	/**
	 * Allows the user to modify controller's data
	 */
	public void optionsButtonClicked(View button) {
		// Inflate the popup menu
		PopupMenu popup = new PopupMenu(fromContext, button);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.context_menu, popup.getMenu());

		// Add listener to the items
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.delete:
						Log.d(TAG, "Delete button clicked");
						Log.e(TAG, "Removing controller");
						// Remove view
						controllerViewManager.removeControllerView(thisController);
						return true;
					case R.id.update:
						Log.d(TAG, "Edit button clicked");
						controllerViewManager.updateControllerView(thisController);
						return true;
					default:
						return false;
				}
			}
		});
		popup.show();
	}

	/**
	 * Sends command via 'ControllerViewEventListener'
	 *
	 * @param data int value to write to Arduino
	 */
	void sendCommand(int data) {
		controllerViewManager.sendCommand(controllerId, controllerType, arduinoPin, pinType, commandType, data);
	}

	/**
	 * Gets the view of the Controller
	 *
	 * @return View
	 */
	public abstract View getView();

	/**
	 * Start the controller
	 */
	public abstract void startController();

	/**
	 * End the controller
	 */
	public abstract void endController();

	/**
	 * Refreshes the name Text view in the Controller View
	 */
	public abstract void updateNameTextView();

	/**
	 * Refreshes the pin Text view in the Controller View
	 */
	public abstract void updatePinTextView();

	/**
	 * Called when data is received
	 *
	 * @param data data received
	 */
	abstract void refreshController(String data, String units);

	public abstract int getControllerData();



	/*   GETTERS AND SETTTERS   */


	public int getControllerId() {
		return this.controllerId;
	}


	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}

	public String getArduinoPin() {
		return arduinoPin;
	}

	public void setArduinoPin(String arduinoPin) {
		this.arduinoPin = arduinoPin;
		updatePinTextView();
	}

	public int getPinType() {
		return pinType;
	}

	public void setPinType(int pinType) {
		this.pinType = pinType;
	}

	public int getCommandType() {
		return commandType;
	}

	public void setCommandType(int commandType) {
		this.commandType = commandType;
	}

	public int getControllerType() {
		return controllerType;
	}

	public void setControllerType(int controllerType) {
		this.controllerType = controllerType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateNameTextView();
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


}
