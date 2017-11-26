package es.udc.psi1718.project.view.customviews.controllers;


public interface ControllerViewEventListener {
	public void controllerSentCommand(String arduinoPin, int pinType, int dataType, int data);
}
