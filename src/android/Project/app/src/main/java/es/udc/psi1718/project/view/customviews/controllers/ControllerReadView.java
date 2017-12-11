package es.udc.psi1718.project.view.customviews.controllers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;

public class ControllerReadView extends ControllerView {
	private String TAG = "ControllerSwitchView";

	private View view;
	private TextView nameTextView;
	private TextView tvData;
	private int REFRESH_RATE = 1000;
	private SendDataThread thread;


	public ControllerReadView(Activity context, int controllerId, String name, int controllerType, String arduinoPin) {
		super(context, controllerId, name, controllerType, arduinoPin,
				arduinoPin.equalsIgnoreCase("digital") ?
						ArduinoCommunicationManager.PINTYPE_DIGITAL :
						ArduinoCommunicationManager.PINTYPE_ANALOG,
				ArduinoCommunicationManager.COMMANDTYPE_READ);
		initializeLayout(name, arduinoPin);
	}

	private void initializeLayout(String name, String arduinoPin) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_read_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		TextView tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		tvData = (TextView) view.findViewById(R.id.tv_controller_data);

		nameTextView.setText(name);
		tvPinNumber.setText("Pin : " + arduinoPin);
	}

	@Override
	void refreshController(String data) {
		final String finalData = data;
		fromContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvData.setText(finalData);
			}
		});
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void setName(String newName) {
		nameTextView.setText(newName);
	}

	@Override
	public void startController() {
		// Start thread
		thread = new SendDataThread(REFRESH_RATE);
		thread.start();
	}

	@Override
	public void endController() {
		// Cancel thread
		if (thread != null) thread.cancel();
	}


	/**
	 * Internal thread with while loop to send data every 'REFRESH_RATE' milliseconds
	 */
	class SendDataThread extends Thread {

		private long refreshRate;
		private Boolean running = true;

		SendDataThread(int refreshRate) {
			this.refreshRate = refreshRate;
		}

		public void cancel() {
			running = false;
		}

		@Override
		public void run() {
			// Create runnable object
			Runnable myRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						while (running) {
							Log.e(TAG, "Sending read request");
							Thread.sleep(refreshRate);
							sendCommand(0);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			// Create new thread based on my runnable object
			new Thread(myRunnable).start();
		}
	}
}
