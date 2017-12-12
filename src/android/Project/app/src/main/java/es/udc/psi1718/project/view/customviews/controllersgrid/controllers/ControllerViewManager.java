package es.udc.psi1718.project.view.customviews.controllersgrid.controllers;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;


public class ControllerViewManager {
	private Activity context;

	private static final String TAG = "ControllerViewManager";

	// Listener interface
	private ControllerViewEventListener listener;

	private ArrayList<ControllerView> controllers;


	public ControllerViewManager(Activity context) {
		this.context = context;
		controllers = new ArrayList<>();
		listener = (ControllerViewEventListener) context;
	}


	/**
	 * Create a new controller view given different parameters
	 *
	 * @param controllerId   id of the controller
	 * @param name           name of the new controller
	 * @param controllerType type of the component that will be attached to the controller
	 * @param arduinoPin     pin where the component is connected
	 * @param pinType        type of the pin (digital-analog)
	 * @param dataType       type of the data (read-write)
	 *
	 * @return {@link ControllerView}
	 */
	public ControllerView createControllerView(int controllerId, String name, int controllerType, String arduinoPin, String pinType, String dataType, int data) {
		ControllerView controllerView;
		switch (controllerType) {

			case ArduinoCommunicationManager.CONTROLLER_GENERIC:
				if (dataType.equalsIgnoreCase("read")) {
					controllerView = new ControllerReadView(this, context, controllerId, name, controllerType, arduinoPin, data);
					break;
				}
				if (pinType.equalsIgnoreCase("digital")) {
					controllerView = new ControllerDigitalWriteView(this, context, controllerId, name, controllerType, arduinoPin, data);
					break;
				} else {
					controllerView = new ControllerAnalogWriteView(this, context, controllerId, name, controllerType, arduinoPin, data);
					break;
				}

			case ArduinoCommunicationManager.CONTROLLER_LED_DIGITAL:
				controllerView = new ControllerDigitalWriteView(this, context, controllerId, name, controllerType, arduinoPin, data);
				break;

			case ArduinoCommunicationManager.CONTROLLER_LED_ANALOG:
				controllerView = new ControllerAnalogWriteView(this, context, controllerId, name, controllerType, arduinoPin, data);
				break;

			case ArduinoCommunicationManager.CONTROLLER_SERVO:
				controllerView = new ControllerAnalogWriteView(this, context, controllerId, name, controllerType, arduinoPin, data);
				break;

			case ArduinoCommunicationManager.CONTROLLER_HUMIDITY_SENSOR:
				controllerView = new ControllerReadView(this, context, controllerId, name, controllerType, arduinoPin, data);
				break;

			case ArduinoCommunicationManager.CONTROLLER_TEMP_SENSOR:
				controllerView = new ControllerReadView(this, context, controllerId, name, controllerType, arduinoPin, data);
				break;

			default:
				return null;
		}

		controllers.add(controllerView);
		return controllerView;
	}

	/**
	 * Remove controllerView
	 *
	 * @param controllerView object to be removed
	 */
	void removeControllerView(ControllerView controllerView) {
		Log.e(TAG, "Removing controller");
		controllers.remove(controllerView);
		// Inform via listener interface that the view was removed
		listener.controllerRemoved(controllerView);
	}


	/**
	 * Sends a command via listener
	 *
	 * @param controllerId   id of the controller
	 * @param controllerType type of the controller
	 * @param arduinoPin     pin number where the controller is
	 * @param pinType        type of pin (digital-analog)
	 * @param commandType    type of command (read-write)
	 * @param data           data to be sent
	 */
	void sendCommand(int controllerId, int controllerType, String arduinoPin, int pinType, int commandType, int data) {
		// Send command via interface
		listener.controllerSentCommand(controllerId, controllerType, arduinoPin, pinType, commandType, data);
	}


	/**
	 * Getter
	 *
	 * @return list of {@link ControllerView}
	 */
	public ArrayList<ControllerView> getControllers() {
		return controllers;
	}

	/**
	 * Called when data is received
	 *
	 * @param panelId      id of the panel
	 * @param controllerId id of the controller
	 * @param data         data received
	 */
	public void receivedData(int panelId, int controllerId, String data) {
		for (ControllerView controller : controllers) {
			controller.receivedData(panelId, controllerId, data);
		}
	}

	/**
	 * Tells the controllers they can start sending data
	 */
	public void startControllers() {
		for (ControllerView controller : controllers) {
			controller.startController();
		}
	}

	/**
	 * Cancels the controllers
	 */
	public void endControllers() {
		for (ControllerView controller : controllers) {
			controller.endController();
		}
	}
}
