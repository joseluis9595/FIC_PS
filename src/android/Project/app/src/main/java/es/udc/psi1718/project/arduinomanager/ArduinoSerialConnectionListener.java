package es.udc.psi1718.project.arduinomanager;

/**
 * Interface that acts as listener for {@link ArduinoUSBCommunicationManager}
 */
public interface ArduinoSerialConnectionListener {

	/**
	 * Called when new data is received from Arduino
	 *
	 * @param panelId      id of the panel
	 * @param controllerId id of the controller
	 * @param data         string received from arduino
	 * @param units        units from the measure taken (null if it has not)
	 */
	void receivedData(int panelId, int controllerId, String data, String units);

	/**
	 * Called when the connection is opened
	 */
	void connectionOpened();

	/**
	 * Called when the connection is closed
	 *
	 * @param arduinoResponseCode response code
	 */
	void connectionClosed(ArduinoResponseCodes arduinoResponseCode);

	/**
	 * Called when connection can´t start
	 *
	 * @param arduinoResponseCode response code
	 */
	void connectionFailed(ArduinoResponseCodes arduinoResponseCode);
}