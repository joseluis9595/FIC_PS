package es.udc.psi1718.project.arduinomanager;


public enum ArduinoResponseCodes {

	// Succesful responses (code > 0)
	RESPONSE_OK(1, "OK"),

	// Error responses (code < 0)
	ERROR_NO_DEVICE(-1, "No devices found"),
	ERROR_NO_COMMUNICATION(-2, "No serial communication available");

	// Internal variables
	private final int code;
	private final String description;


	/**
	 * Constructor
	 *
	 * @param code        response Code
	 * @param description response Description
	 */
	ArduinoResponseCodes(final int code, final String description) {
		this.code = code;
		this.description = description;
	}


	/* GETTERS */

	public int getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
}