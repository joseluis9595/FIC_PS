package es.udc.psi1718.project.view.customviews.controllers;

import android.content.Context;

import java.util.ArrayList;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;


public class ControllerViewManager {
	private Context context;

	private ArrayList<ControllerView> controllers;

	public ControllerViewManager(Context context) {
		this.context = context;
		controllers = new ArrayList<>();
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
	public ControllerView createControllerView(int controllerId, String name, int controllerType, String arduinoPin, String pinType, String dataType) {
		ControllerView controllerView;
		switch (controllerType) {

			case ArduinoCommunicationManager.CONTROLLER_GENERIC:
				if (dataType.equalsIgnoreCase("read")) {
					controllerView = new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);
					break;
				}
				if (pinType.equalsIgnoreCase("digital")) {
					controllerView = new ControllerDigitalWriteView(context, controllerId, name, controllerType, arduinoPin);
					break;
				} else {
					controllerView = new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);
					break;
				}

			case ArduinoCommunicationManager.CONTROLLER_LED_DIGITAL:
				controllerView = new ControllerDigitalWriteView(context, controllerId, name, controllerType, arduinoPin);
				break;

			case ArduinoCommunicationManager.CONTROLLER_LED_ANALOG:
				controllerView = new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);
				break;

			case ArduinoCommunicationManager.CONTROLLER_SERVO:
				controllerView = new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);
				break;

			case ArduinoCommunicationManager.CONTROLLER_HUMIDITY_SENSOR:
				controllerView = new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);
				break;

			case ArduinoCommunicationManager.CONTROLLER_TEMP_SENSOR:
				controllerView = new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);
				break;

			default:
				return null;
		}

		controllers.add(controllerView);
		return controllerView;
	}

	public ArrayList<ControllerView> getControllers() {
		return controllers;
	}

	// /**
	//  * Called when data is received
	//  *
	//  * @param panelId      id of the panel
	//  * @param controllerId id of the controller
	//  * @param data         data received
	//  */
	// public void refreshController(int panelId, int controllerId, String data) {
	// 	for (ControllerView controller : controllers) {
	// 		controller.refreshController(panelId, controllerId, data);
	// 	}
	// }
}
