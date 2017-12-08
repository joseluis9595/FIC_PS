package es.udc.psi1718.project.view.customviews.controllers;


import android.content.Context;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;

public class ControllerViewManager {

	private Context context;

	public ControllerViewManager(Context context) {
		this.context = context;
	}

	public ControllerView createControllerView(String name, int controllerType, String arduinoPin, String pinType, String dataType) {
		switch (controllerType) {

			case ArduinoCommunicationManager.CONTROLLER_GENERIC:
				if (dataType.equalsIgnoreCase("read"))
					return new ControllerReadView(context, name, controllerType, arduinoPin);
				if (pinType.equalsIgnoreCase("digital")) {
					return new ControllerDigitalWriteView(context, name, controllerType, arduinoPin);
				} else {
					return new ControllerAnalogWriteView(context, name, controllerType, arduinoPin);
				}

			case ArduinoCommunicationManager.CONTROLLER_LED_DIGITAL:
				return new ControllerDigitalWriteView(context, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_LED_ANALOG:
				return new ControllerAnalogWriteView(context, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_SERVO:
				return new ControllerAnalogWriteView(context, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_HUMIDITY_SENSOR:
				return new ControllerReadView(context, name, controllerType, arduinoPin);

			case ArduinoCommunicationManager.CONTROLLER_TEMP_SENSOR:
				return new ControllerReadView(context, name, controllerType, arduinoPin);

			default:
				return null;
		}
	}
}
