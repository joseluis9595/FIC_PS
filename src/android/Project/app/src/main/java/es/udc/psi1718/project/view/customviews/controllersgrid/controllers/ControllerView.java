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

	public ControllerView(ControllerViewManager manager, Activity context, int controllerId,
						  String name, int controllerType, String arduinoPin, int pinType, int commandType) {
		super(context);
		this.fromContext = context;
		this.controllerId = controllerId;
		this.name = name;
		this.controllerType = controllerType;
		this.arduinoPin = arduinoPin;
		this.pinType = pinType;
		this.commandType = commandType;
		controllerViewManager = manager;
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
	 * Allows the user to modify controller's data
	 */
	public void editController(View button) {
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
	 * Getter
	 *
	 * @return controllerId
	 */
	public int getControllerId() {
		return this.controllerId;
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
	public abstract void updateNameTextView(String newName);

	// /**
	//  * Changes the main Text view in the Controller View
	//  *
	//  * @param newPin the new name you want for the Controller
	//  */
	// public abstract void updatePinNumberTextView(String newPin);

	public abstract void startController();

	public abstract void endController();

	/**
	 * Called when data is received
	 *
	 * @param data data received
	 */
	abstract void refreshController(String data);

	public abstract int getControllerData();

}
