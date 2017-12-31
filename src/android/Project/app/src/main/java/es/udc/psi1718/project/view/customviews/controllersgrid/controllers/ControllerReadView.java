package es.udc.psi1718.project.view.customviews.controllersgrid.controllers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoUSBCommunicationManager;

public class ControllerReadView extends ControllerView {
	private String TAG = "ControllerSwitchView";

	// LAyout variables
	private View view;
	private TextView nameTextView;
	private TextView tvData;
	private ImageButton btnEdit;

	// Thread variables
	private int REFRESH_RATE = 1000;
	private SendDataThread thread;
	private TextView tvPinNumber;


	public ControllerReadView(ControllerViewManager manager, Activity context, int controllerId,
							  String name, int controllerType, String arduinoPin, int data) {
		super(manager, context, controllerId, name, controllerType, arduinoPin,
				arduinoPin.equalsIgnoreCase("digital") ?
						ArduinoUSBCommunicationManager.PINTYPE_DIGITAL :
						ArduinoUSBCommunicationManager.PINTYPE_ANALOG,
				ArduinoUSBCommunicationManager.COMMANDTYPE_READ, data);
		initializeLayout(name, arduinoPin, data);
	}

	private void initializeLayout(String name, String arduinoPin, int data) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_read_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		tvData = (TextView) view.findViewById(R.id.tv_controller_data);
		btnEdit = (ImageButton) view.findViewById(R.id.btn_controllerview_edit);

		// Add click listener to the 3-dotted button
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				optionsButtonClicked(btnEdit);
			}
		});

		// Modify the view
		nameTextView.setText(String.valueOf(name));
		tvPinNumber.setText("Pin : " + arduinoPin);
		tvData.setText(String.valueOf(data));
	}

	@Override
	void refreshController(String data, String units) {
		final String finalData = data;
		if (units == null) {
			fromContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvData.setText(finalData);
				}
			});
		} else {
			int dataProcessed = Integer.valueOf(data);
			final String dataWithUnits = dataProcessed + units;

			fromContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvData.setText(dataWithUnits);
				}
			});
		}


	}

	@Override
	public int getControllerData() {
		return 0;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void updateNameTextView() {
		nameTextView.setText(getName());
	}

	@Override
	public void updatePinTextView() {
		tvPinNumber.setText(getArduinoPin());
	}

	@Override
	public void startController() {
		// Start thread
		tvData.setAlpha(1f);
		thread = new SendDataThread(REFRESH_RATE);
		thread.start();
	}

	@Override
	public void endController() {
		// Cancel thread
		if (thread != null) {
			thread.cancel();
			tvData.setAlpha(0.3f);
		}
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
