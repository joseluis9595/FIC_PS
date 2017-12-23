package es.udc.psi1718.project.arduinomanager;


public abstract class AbstractArduinoCommunicationManager {

	// Pin and command type
	public final static int PINTYPE_DIGITAL = 1;
	public final static int PINTYPE_ANALOG = 2;
	public final static int COMMANDTYPE_READ = 3;
	public final static int COMMANDTYPE_WRITE = 4;

	// Controller types
	public final static int CONTROLLER_GENERIC = 0;
	public final static int CONTROLLER_LED_ANALOG = 1;
	public final static int CONTROLLER_LED_DIGITAL = 2;
	public final static int CONTROLLER_SERVO = 3;
	public final static int CONTROLLER_TEMP_SENSOR = 4;
	public final static int CONTROLLER_HUMIDITY_SENSOR = 5;
	public final static int CONTROLLER_LIGHT_SENSOR = 6;

	/**
	 * Starts the communication with Arduino
	 */
	public abstract ArduinoResponseCodes startCommunication();

	/**
	 * Ends communication with arduino
	 */
	public abstract void endCommunication();

	/**
	 * Sends command via serial port
	 *
	 * @param controllerId   id of the controller that sent the command
	 * @param controllerType type of the controller
	 * @param arduinoPin     pin where the component is connected on the arduino
	 * @param pinType        type of the pin (digital-analog)
	 * @param commandType    command type (read-write)
	 * @param data           data sent
	 *
	 * @return {@link ArduinoResponseCodes}
	 */
	public abstract ArduinoResponseCodes sendCommand(int controllerId, int controllerType, String arduinoPin, int pinType, int commandType, int data);


}
