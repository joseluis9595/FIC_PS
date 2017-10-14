package es.udc.psi1718.project.ui.customviews;


public interface ControllerViewEventListener {
	public void controllerChangedState(int arduinoPin, int pinType, int dataType, int data);
}
