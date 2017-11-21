package es.udc.psi1718.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.util.UserPreferencesManager;


public class MyBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "MyBroadcastReceiver";

	// TODO solucionar esto...
	private ArduinoCommunicationManager arduinoCommunicationManager;
	private ControllersActivity controllersActivity;

	public MyBroadcastReceiver() {
	}

	public MyBroadcastReceiver(ControllersActivity controllersActivity, ArduinoCommunicationManager arduinoCommunicationManager) {
		super();
		this.controllersActivity = controllersActivity;
		this.arduinoCommunicationManager = arduinoCommunicationManager;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		UsbDevice device = null;

		Log.d(TAG, "BROADCAST : is called : " + action);

		switch (action) {

			case UsbManager.ACTION_USB_DEVICE_DETACHED:
				device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null) {
					int deviceID = device.getVendorId();
					if (deviceID == 0x2341) {
						Log.d(TAG, "BROADCAST : USB device detached");
						arduinoCommunicationManager.closeConnection();
					}
				}
				break;

			case UsbManager.ACTION_USB_DEVICE_ATTACHED:
				// Check if user wants to start connection automatically
				if (UserPreferencesManager.getInstance(context).getStartConnectionAutomatically()) {
					Log.d(TAG, "StartCommunicationAuto : True");
					device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null) {
						int deviceID = device.getVendorId();
						if (deviceID == 0x2341) {
							Log.d(TAG, "BROADCAST : USB device attached");

							// If activity is active, call startComm method, else, start activity with extras
							if (ControllersActivity.active) {
								controllersActivity.startCommunication();
							} else if (!MainActivity.active) {
								Intent mainActivityIntent = new Intent(context, MainActivity.class);
								mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								// mainActivityIntent.putExtra(Constants.INTENTCOMM_CONTACTIV_LAUNCHEDFROMBR, true);
								context.startActivity(mainActivityIntent);
							}
						}
					}
				}
				break;
			default:
				break;
		}

	}
}