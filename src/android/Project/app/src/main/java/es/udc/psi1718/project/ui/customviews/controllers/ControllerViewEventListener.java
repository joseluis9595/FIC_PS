package es.udc.psi1718.project.ui.customviews.controllers;


public interface ControllerViewEventListener {
	public void controllerSentCommand(String arduinoPin, int pinType, int dataType, int data);
}
