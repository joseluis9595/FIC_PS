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

	// Interface
	private ArduinoSerialListener arduinoSerialListener;

	// USB communication
	private UsbManager usbManager;
	private UsbDevice device;
	private UsbSerialDevice serialPort;
	private UsbDeviceConnection connection;


	// TODO change int response codes to (int, String) type codes
	public int ERR_NO_COMMUNICATION = -2;
	public int ERR_NO_DEVICE = -1;
	public int OK_CODE = 1;
	public int CONNECTION_OPENED = 2;
	public int CONNECTION_CLOSED = 3;

	// Reading via Serial port variables
	private byte iNBbyte = 8;
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


	/**
	 * Sends command via Serial Port
	 *
	 * @param command
	 *
	 * @return
	 */
	public int sendCommand(String command) {
		if (serialPort != null) {
			serialPort.write(command.getBytes());
			Log.d(TAG, "SENDCOMMAND : sending command");
			return OK_CODE;
		} else {
			Log.e(TAG, "SENDCOMMAND : serialPort null");
			return ERR_NO_COMMUNICATION;
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
		if (firstChar == '*') { // check if it works
			return true;
		} else {
			return false;
		}
	}


	/**
	 * 'UsbReadCallback' auxiliary function
	 */
	private void clearBytes() {
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
	private void sendData() {
		try {

			String aux = new String(buffer, "UTF-8");    // Creating String with the read buffer
			String finalData = aux.substring(0, 9);      // We just need the first 9 chars

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

					if (bufferSize >= iNBbyte && startbytefound) {
						startbytefound = false;
						Log.d(TAG, "mCalback : FOUND WORD");
						bufferSize = iNBbyte;
						byte[] buf = new byte[iNBbyte];
						System.arraycopy(buffer, 0, buf, 0, bufferSize);

						sendData(); //process the data
					}
				}
			}
		}
	};


	/**
	 * Asks for permission to access usb device
	 */
	public int startCommunication() {
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
					return OK_CODE;
			}
			Log.e(TAG, "STARTCOMM : Arduino not found");
			return ERR_NO_DEVICE;
		} else {
			Log.e(TAG, "STARTCOMM : No USB devices");
			return ERR_NO_DEVICE;
		}
	}


	/**
	 * Broadcast Receiver to automatically start and stop the Serial connection.
	 */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(ACTION_USB_PERMISSION)) {
				boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
				if (granted) {
					Log.d(TAG, "BROADCAST : Permission Granted");
					openConnection(device, 9600);
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
		if (device == null) {
			Log.e(TAG, "OPENCONN : Device is null");
			return;
		}

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
			} else {
				// TODO display error
				Log.e(TAG, "OPENCONNECTION : Serial port is not opened");
			}
		} else {
			// TODO display error
			Log.e(TAG, "OPENCONNECTION : Port is null");
		}
	}


	/**
	 * Close serial communication with device
	 */
	public int closeConnection() {
		if (serialPort != null) {
			serialPort.close();
			Log.d(TAG, "CLOSECONN : Serial connection closed");
			arduinoSerialListener.connectionClosed();
			return OK_CODE;
		} else {
			Log.d(TAG, "CLOSECONN : No connection to close");
			return ERR_NO_COMMUNICATION;
		}


	}
}
