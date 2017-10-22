package es.udc.psi1718.project.arduinomanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import es.udc.psi1718.project.util.Constants;

public class ArduinoBroadcastReceiverService extends Service {

	private final String TAG = "ArduinoBRService";

	private IntentFilter intentFilter;

	// Thread
	private Thread backgroundThread = null;

	// Constants
	private final int DEFAULT_COUNT = 20;
	private final int DEFAULT_TIME_WAIT = 20;

	// Numeric variables
	private int count;
	private int timeWait;


	public ArduinoBroadcastReceiverService() {
		Log.d(TAG, "Constructor");

		intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_USB_PERMISSION);
		intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);


	}


	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		registerReceiver(broadcastReceiver, intentFilter);
		Log.d(TAG, "Registering receiver...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);

	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d(TAG, "onRebind");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "onStart");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");


		// Call super function
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		stopThread();
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
		Log.d(TAG, "Unregistering receiver...");
	}

	/**
	 * Function to stop the background thread
	 */
	private void stopThread() {
		if (backgroundThread != null) {
			Log.d(TAG, "StopThread : Stopping thread...");
			backgroundThread.interrupt();
			backgroundThread = null;
		}
	}




	/* INTERNAL CLASSES */

	/**
	 * Broadcast Receiver to automatically start and stop the Serial connection.
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO IT1 comprobar por que no se activa el Broadcast Receiver cuando se quiere iniciar una conexión (tarda mucho)
			String action = intent.getAction();

			Log.d(TAG, "BROADCAST : is called : " + action);
			Toast.makeText(context, "Called : " + action, Toast.LENGTH_SHORT).show();

			switch (action) {
				case Constants.ACTION_USB_PERMISSION:
					boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
					if (granted) {
						Log.d(TAG, "BROADCAST : Permission Granted");
						// TODO IT1 solve this - openConnection(device, 9600);
						Log.d(TAG, "BROADCAST : Open connection is called");
					} else {
						Log.e(TAG, "BROADCAST : Permission not granted");
					}
					break;
				case UsbManager.ACTION_USB_DEVICE_DETACHED:
					UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null) {
						int deviceID = device.getVendorId();
						if (deviceID == 0x2341) {
							Log.d(TAG, "BROADCAST : USB device detached");
							// TODO IT 1solve this - closeConnection();
						}
					}
					break;
				default:
					break;
			}

		}
	};

}
