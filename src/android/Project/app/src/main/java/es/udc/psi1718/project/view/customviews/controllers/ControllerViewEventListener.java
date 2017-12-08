package es.udc.psi1718.project.view.customviews.controllers;

/**
 * Interface that acts as listener for {@link ControllerView} events
 */
public interface ControllerViewEventListener {
	/**
	 * Indicates that a controller sent a command
	 *
	 * @param controllerType type of the controller
	 * @param arduinoPin     pin of the controller
	 * @param pinType        type of the pin of the controller
	 * @param dataType       type of the data
	 * @param data           data
	 */
	void controllerSentCommand(int controllerType, String arduinoPin, int pinType, int dataType, int data);
}
