package es.udc.psi1718.project.arduinomanager;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.USB_SERVICE;
import static es.udc.psi1718.project.util.Constants.ACTION_USB_PERMISSION;

/**
 * In this class we implement all the USB connection and communication with Arduino
 */
public class ArduinoCommunicationManager {

	private final String TAG = "ArduinoCommunication";
	private Context fromContext;

	// Constants
	private final int ARDUINO_VENDORID = 0x2341;

	// private IntentFilter filter;
	// private BroadcastReceiver broadcastReceiver;

	// String command
	private final char COMMAND_FIRST_BYTE = '*';
	private final char COMMAND_SEPARATOR = '-';
	private final int COMMAND_DATA_LENGTH = 4;
	private final int COMMAND_ARDUINO_PIN_LENGTH = 2;
	private final int COMMAND_LENGTH = 12;

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

	// Interface
	private ArduinoSerialListener arduinoSerialListener;

	// USB communication
	private UsbManager usbManager;
	private UsbSerialDevice serialPort;
	private UsbDeviceConnection connection;


	// Reading via Serial port variables
	private byte[] buffer = new byte[0];
	private int bufferSize;
	private Boolean startbytefound = false;


	/**
	 * Constructor
	 *
	 * @param fromContext
	 */
	public ArduinoCommunicationManager(Context fromContext) {
		this.fromContext = fromContext;
		usbManager = (UsbManager) fromContext.getSystemService(USB_SERVICE);
		arduinoSerialListener = (ArduinoSerialListener) fromContext;
		// createBroadcastReceiver();
	}

	// private void createBroadcastReceiver() {
	// 	filter = new IntentFilter();
	// 	filter.addAction(ACTION_USB_PERMISSION);

	private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO IT1 comprobar por que no se activa el Broadcast Receiver cuando se quiere iniciar una conexión (tarda mucho)
			String action = intent.getAction();

			Log.d(TAG, "mPermissionReceiver : is called : " + action);

			switch (action) {
				case ACTION_USB_PERMISSION:
					boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
					if (!granted) {
						Log.e(TAG, "mPermissionReceiver : Permission not granted");
						arduinoSerialListener.connectionFailed(ArduinoResponseCodes.ERROR_PERMISSION_DENIED);
					} else {
						Log.d(TAG, "mPermissionReceiver : Permission Granted");
						UsbDevice dev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
						if (dev != null) {
							if (dev.getVendorId() == ARDUINO_VENDORID) {
								openConnection(dev, 9600);
								Log.d(TAG, "mPermissionReceiver : Open connection is called");
								return;
							} else {
								arduinoSerialListener.connectionFailed(ArduinoResponseCodes.ERROR_NO_ARDUINO_DEVICE);
							}
						} else {
							Log.e(TAG, "mPermissionReceiver : device not present!");
							arduinoSerialListener.connectionFailed(ArduinoResponseCodes.ERROR_NO_DEVICE);
						}

					}
					Log.e(TAG, "mPermissionReceiver : UNREGISTER RECEIVER PERMISSION");
					fromContext.unregisterReceiver(mPermissionReceiver);
					break;
				default:
					break;
			}

		}
	};


	/**
	 * Creates a String command that will be sent via serial port to the arduino
	 *
	 * @param controllerType type of component that the controller represents
	 * @param arduinoPin     pin where the component is connected
	 * @param pinType        type of the pin (analog-digital)
	 * @param commandType    type of command (write-read)
	 * @param data           data to be sent
	 *
	 * @return String
	 */
	private String createCommand(int controllerType, String arduinoPin, int pinType, int commandType, int data) {
		String command = "";
		command += COMMAND_FIRST_BYTE;

		// TODO handle errors properly
		// PinType (Analog or Digital)
		switch (pinType) {
			case PINTYPE_ANALOG:
				command += "A";
				break;
			case PINTYPE_DIGITAL:
				command += "D";
				break;
			default:
				return "ERROR";
		}
		command += COMMAND_SEPARATOR;

		// Command Type (Read or Write)
		switch (commandType) {
			case COMMANDTYPE_READ:
				command += "R";
				break;
			case COMMANDTYPE_WRITE:
				command += "W";
				break;
			default:
				return "ERROR";
		}
		command += COMMAND_SEPARATOR;

		// arduinoPin
		int arduinoPinLength = arduinoPin.length();
		if (arduinoPinLength == 1) {
			command += "0";
		} else if (arduinoPinLength > 2 || arduinoPinLength < 1) {
			return "ERROR";
		}
		command += arduinoPin;
		command += COMMAND_SEPARATOR;

		// Data (0-1024)
		int dataLength = String.valueOf(data).length();
		int dataZeroes = COMMAND_DATA_LENGTH - dataLength;
		if (dataZeroes <= 0) dataZeroes = 0;
		while (dataZeroes > 0) {
			command += "0";
			dataZeroes -= 1;
		}
		command += data;

		// Check if it has the proper length
		if (command.length() != COMMAND_LENGTH) {
			return "ERROR";
		}
		Log.d(TAG, "Command : " + command);

		return command;

	}


	/**
	 * Sends command via serial port
	 *
	 * @param controllerType type of the controller
	 * @param arduinoPin     pin where the component is connected on the arduino
	 * @param pinType        type of the pin (digital-analog)
	 * @param commandType    command type (read-write)
	 * @param data           data sent
	 *
	 * @return {@link ArduinoResponseCodes}
	 */
	public ArduinoResponseCodes sendCommand(int controllerType, String arduinoPin, int pinType, int commandType, int data) {
		if (serialPort != null) {
			String command = createCommand(controllerType, arduinoPin, pinType, commandType, data);

			// If malformed command send error via interface
			if (command.equals("ERROR")) return ArduinoResponseCodes.ERROR_INVALID_COMMAND;

			Log.d(TAG, "SENDCOMMAND : sending command - " + command);
			serialPort.write(command.getBytes());
			return ArduinoResponseCodes.RESPONSE_OK;
		} else {
			Log.e(TAG, "SENDCOMMAND : serialPort null");
			return ArduinoResponseCodes.ERROR_NO_COMMUNICATION;
		}
	}


	/**
	 * 'UsbReadCallback' auxiliary function
	 *
	 * @param firstChar
	 *
	 * @return
	 */
	private boolean isStartByte(byte firstChar) {
		// TODO IT3 Comprobar si funciona bien cambiando '*' por la constante COMMAND_FIRST_BYTE
		if (firstChar == '*') {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * 'UsbReadCallback' auxiliary function
	 */
	private void clearBytes() {
		// TODO IT3 corregir el tamaño del buffer?
		buffer = new byte[40];
		bufferSize = 0;
	}


	/**
	 * 'UsbReadCallback' auxiliary function
	 *
	 * @param buf
	 */
	private void appendBytes(byte[] buf) {
		System.arraycopy(buf, 0, buffer, bufferSize, buf.length);
		bufferSize += buf.length;
	}


	/**
	 * Crops the RAW data read via Serial port and sends it through the interface
	 */
	private void notifyReceivedData() {
		try {

			String aux = new String(buffer, "UTF-8");    // Creating String with the read buffer
			String finalData = aux.substring(0, COMMAND_LENGTH);      // We just need the first 9 chars

			// Sending data through interface
			arduinoSerialListener.receivedData("Received : " + finalData + "\n");
			Log.e(TAG, "checkData : received RAW String : " + finalData);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	/**
	 * USBSerial callback. Called when data is received via USB
	 */
	private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
		//Defining a Callback which triggers whenever data is read.
		@Override
		public void onReceivedData(byte[] arg0) {
			// Log.d(TAG, "Received data");
			if (arg0 != null) {
				// Log.e(TAG, "Data received is null");
				if (arg0.length > 0) {
					// Log.e(TAG, "Data received has length > 0");
					if (isStartByte(arg0[0]) && !startbytefound) { //look if its a new frame
						Log.d(TAG, "mCallback : starting word");
						startbytefound = true;
						clearBytes(); // clears my buffer
					}

					if (startbytefound) {
						appendBytes(arg0);
					}

					if (bufferSize >= COMMAND_LENGTH - 1 && startbytefound) {
						startbytefound = false;
						Log.d(TAG, "mCalback : FOUND WORD");
						bufferSize = COMMAND_LENGTH;
						byte[] buf = new byte[COMMAND_LENGTH];
						System.arraycopy(buffer, 0, buf, 0, bufferSize);

						notifyReceivedData(); //process the data
					}
				} else {
					// Log.e(TAG, "Data received has no length");
				}
			} else {
				// Log.e(TAG, "Data received is null");
			}
		}
	};


	/**
	 * Asks for permission to access usb device
	 */
	public ArduinoResponseCodes startCommunication() {
		Log.d(TAG, "startCommunication called");
		HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
		PendingIntent pi = PendingIntent.getBroadcast(fromContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

		// TODO allow only one call to this function at a time

		if (!usbDevices.isEmpty()) {
			for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
				UsbDevice device = entry.getValue();
				if (device.getVendorId() == ARDUINO_VENDORID) {

					Log.e(TAG, "startCommunication : REGISTER RECEIVER PERMISSION");
					fromContext.registerReceiver(mPermissionReceiver, new IntentFilter(
							ACTION_USB_PERMISSION));

					if (usbManager.hasPermission(device)) {
						Log.d(TAG, "startCommunication : Already has permission");
						// usbManager.requestPermission(device, pi);
						openConnection(device, 9600);
						// Log.e(TAG, "startCommunication : UNREGISTER RECEIVER PERMISSION");
						// fromContext.unregisterReceiver(mPermissionReceiver);
					} else {
						Log.d(TAG, "startCommunication : Does not have permission");
						usbManager.requestPermission(device, pi);
						Log.d(TAG, "startCommunication : Se ejecuta justo después");
					}

					Log.d(TAG, "STARTCOMM : Found Arduino device");
					return ArduinoResponseCodes.RESPONSE_OK;

				} else {
					Log.d(TAG, "STARTCOMM : Not Arduino device");
					//connection = null;
					device = null;
				}
			}
			Log.e(TAG, "STARTCOMM : Arduino not found");
			return ArduinoResponseCodes.ERROR_NO_DEVICE;
		} else {
			Log.e(TAG, "STARTCOMM : No USB devices");
			return ArduinoResponseCodes.ERROR_NO_DEVICE;
		}
	}


	/**
	 * Open a new Serial communication with device
	 */
	private void openConnection(UsbDevice device, int baudioRate) {

		// Unregister the permissions receiver
		Log.e(TAG, "startCommunication : UNREGISTER RECEIVER PERMISSION");
		fromContext.unregisterReceiver(mPermissionReceiver);

		Log.d(TAG, "OPENCONN : IS CALLED");
		if (device == null) {
			Log.e(TAG, "OPENCONN : Device is null");
			return;
		}

		Log.d(TAG, "OPENCONN : Device is not null");

		// Start connection
		connection = usbManager.openDevice(device);
		serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
		if (serialPort != null) {
			if (serialPort.open()) { //Set Serial Connection Parameters.
				//setUiEnabled(true);
				serialPort.setBaudRate(baudioRate);
				serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
				serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
				serialPort.setParity(UsbSerialInterface.PARITY_NONE);
				serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
				serialPort.read(mCallback);
				arduinoSerialListener.connectionOpened();

				//tvAppend(textView, "Serial Connection Opened!\n");
				Log.d(TAG, "OPENCONNECTION : Connection opened");
				return;
			} else {
				Log.e(TAG, "OPENCONNECTION : Serial port is not opened");
			}
		} else {
			Log.e(TAG, "OPENCONNECTION : Port is null");
		}
		arduinoSerialListener.connectionFailed(ArduinoResponseCodes.ERROR_NO_COMMUNICATION);
	}


	/**
	 * Close serial communication with device
	 */
	public void closeConnection() {
		if (serialPort != null) {
			serialPort.close();
			Log.d(TAG, "CLOSECONN : Serial connection closed");
			arduinoSerialListener.connectionClosed(ArduinoResponseCodes.RESPONSE_OK);
		} else {
			Log.d(TAG, "CLOSECONN : No connection to close");
			arduinoSerialListener.connectionClosed(ArduinoResponseCodes.ERROR_NO_COMMUNICATION);
			//return ArduinoResponseCodes.ERROR_NO_COMMUNICATION;
		}

	}
}
