package es.udc.psi1718.project.view.customviews.controllers;

import android.content.Context;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;


public class ControllerViewManager {
	private Context context;

	public ControllerViewManager(Context context) {
		this.context = context;
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
		switch (controllerType) {

			case ArduinoCommunicationManager.CONTROLLER_GENERIC:
				if (dataType.equalsIgnoreCase("read"))
					return new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);
				if (pinType.equalsIgnoreCase("digital")) {
					return new ControllerDigitalWriteView(context, controllerId, name, controllerType, arduinoPin);
				} else {
					return new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);
				}

			case ArduinoCommunicationManager.CONTROLLER_LED_DIGITAL:
				return new ControllerDigitalWriteView(context, controllerId, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_LED_ANALOG:
				return new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_SERVO:
				return new ControllerAnalogWriteView(context, controllerId, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_HUMIDITY_SENSOR:
				return new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_TEMP_SENSOR:
				return new ControllerReadView(context, controllerId, name, controllerType, arduinoPin);

			default:
				return null;
		}
	}
}
