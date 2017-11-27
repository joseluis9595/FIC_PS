package es.udc.psi1718.project.arduinomanager;

/**
 * Interface that acts as listener for {@link ArduinoCommunicationManager}
 */
public interface ArduinoSerialListener {

	/**
	 * Called when new data is received from Arduino
	 *
	 * @param data string received from arduino
	 */
	void receivedData(String data);

	/**
	 * Called when the connection is opened
	 */
	void connectionOpened();

	/**
	 * Called when the connection is closed
	 */
	void connectionClosed(ArduinoResponseCodes arduinoResponseCode);

	/**
	 * Called when connection can´t start
	 */
	void connectionFailed(ArduinoResponseCodes arduinoResponseCode);
}