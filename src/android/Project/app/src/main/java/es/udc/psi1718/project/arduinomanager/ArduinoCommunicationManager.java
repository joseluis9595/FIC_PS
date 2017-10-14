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

/**
 * In this class we implement all the USB connection and communication with Arduino
 */
public class ArduinoCommunicationManager {

	private final String TAG = "ArduinoCommunication";
	private final String ACTION_USB_PERMISSION = "es.udc.psi1718.project.USB_PERMISSION";
	private Context fromContext;

	private IntentFilter filter;

	// String command
	private final char COMMAND_FIRST_BYTE = '*';
	private final char COMMAND_SEPARATOR = '-';
	private final int COMMAND_DATA_LENGTH = 4;
	private final int COMMAND_ARDUINO_PIN_LENGTH = 2;
	private final int COMMAND_LENGTH = 12;

	public final static int PINTYPE_DIGITAL = 1;
	public final static int PINTYPE_ANALOG = 2;
	public final static int DATATYPE_READ = 3;
	public final static int DATATYPE_WRITE = 4;

	// Interface
	private ArduinoSerialListener arduinoSerialListener;

	// USB communication
	private UsbManager usbManager;
	private UsbDevice device;
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
		filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		arduinoSerialListener = (ArduinoSerialListener) fromContext;

	}

	public BroadcastReceiver getBroadcastReceiver() {
		return this.broadcastReceiver;
	}

	public IntentFilter getBroadcastFilter() {
		return this.filter;
	}


	private String createCommand(int arduinoPin, int pinType, int dataType, int data) {
		String command = "";
		command += COMMAND_FIRST_BYTE;

		// PinType (Analog or Digital)
		switch (pinType) {
			case PINTYPE_ANALOG:
				command += "A";
				break;
			case PINTYPE_DIGITAL:
				command += "D";
				break;
			default:
				// TODO handle errors
				return "ERROR";
		}
		command += COMMAND_SEPARATOR;

		// Command Type (Read or Write)
		switch (dataType) {
			case DATATYPE_READ:
				command += "R";
				break;
			case DATATYPE_WRITE:
				command += "W";
				break;
			default:
				// TODO handle errors
				return "ERROR";
		}
		command += COMMAND_SEPARATOR;

		// arduinoPin
		int arduinoPinLength = String.valueOf(arduinoPin).length();
		if (arduinoPinLength == 1) {
			command += "0";
		} else if (arduinoPinLength > 2 || arduinoPinLength < 1) {
			// TODO handle errors
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
			// TODO handle errors
			return "ERROR";
		}

		return command;

	}


	/**
	 * Sends command via serial port
	 *
	 * @param arduinoPin
	 * @param pinType
	 * @param dataType
	 * @param data
	 *
	 * @return
	 */
	public ArduinoResponseCodes sendCommand(int arduinoPin, int pinType, int dataType, int data) {
		// TODO arduinoPin should not be just numerical (ex.: you can have A0)
		if (serialPort != null) {
			String command = createCommand(arduinoPin, pinType, dataType, data);
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
		// TODO Comprobar si funciona bien cambiando '*' por la constante COMMAND_FIRST_BYTE
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
		// TODO corregir el tamaño del buffer?
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
			Log.d(TAG, "Received data");
			if (arg0 != null) {
				if (arg0.length > 0) {
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
				}
			}
		}
	};


	/**
	 * Asks for permission to access usb device
	 */
	public ArduinoResponseCodes startCommunication() {
		HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

		if (!usbDevices.isEmpty()) {
			boolean found = false;
			for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
				device = entry.getValue();
				int deviceID = device.getVendorId();
				if (deviceID == 0x2341) { //Arduino Vendor ID
					PendingIntent pi = PendingIntent.getBroadcast(fromContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
					usbManager.requestPermission(device, pi);
					Log.d(TAG, "STARTCOMM : Found Arduino device");
					found = true;
				} else {
					Log.d(TAG, "STARTCOMM : Not Arduino device");
					//connection = null;
					device = null;
				}

				if (found)
					return ArduinoResponseCodes.RESPONSE_OK;
			}
			Log.e(TAG, "STARTCOMM : Arduino not found");
			return ArduinoResponseCodes.ERROR_NO_DEVICE;
		} else {
			Log.e(TAG, "STARTCOMM : No USB devices");
			return ArduinoResponseCodes.ERROR_NO_DEVICE;
		}
	}


	/**
	 * Broadcast Receiver to automatically start and stop the Serial connection.
	 */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO comprobar por que no se activa el Broadcast Receiver cuando se quiere iniciar una conexión (tarda mucho)
			String action = intent.getAction();

			Log.d(TAG, "BROADCAST : is called : " + action);

			if (action.equals(ACTION_USB_PERMISSION)) {
				boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
				if (granted) {
					Log.d(TAG, "BROADCAST : Permission Granted");
					openConnection(device, 9600);
					Log.d(TAG, "BROADCAST : Open connection is called");
				} else {
					Log.e(TAG, "BROADCAST : Permission not granted");
				}
			} else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
				UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null) {
					int deviceID = device.getVendorId();
					if (deviceID == 0x2341) {
						Log.d(TAG, "BROADCAST : USB device detached");
						closeConnection();
					}
				}
			}
		}
	};


	/**
	 * Open a new Serial communication with device
	 */
	private void openConnection(UsbDevice device, int baudioRate) {
		Log.d(TAG, "OPENCONN : IS CALLED");
		if (device == null) {
			Log.e(TAG, "OPENCONN : Device is null");
			return;
		}

		Log.d(TAG, "OPENCONN : Device is not null");

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
	public ArduinoResponseCodes closeConnection() {
		if (serialPort != null) {
			serialPort.close();
			Log.d(TAG, "CLOSECONN : Serial connection closed");
			arduinoSerialListener.connectionClosed();
		} else {
			Log.d(TAG, "CLOSECONN : No connection to close");
			//return ArduinoResponseCodes.ERROR_NO_COMMUNICATION;
		}
		return ArduinoResponseCodes.RESPONSE_OK;


	}
}
