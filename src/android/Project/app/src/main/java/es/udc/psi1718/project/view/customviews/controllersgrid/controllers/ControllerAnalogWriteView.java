package es.udc.psi1718.project.view.customviews.controllersgrid.controllers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoUSBCommunicationManager;
import es.udc.psi1718.project.util.Constants;


public class ControllerAnalogWriteView extends ControllerView {

	private String TAG = "ControllerAnalogWriteView";

	// Layout variables
	private View view;
	private TextView nameTextView;

	// Temporal fix for sliders
	private long timeInMillis;
	private ImageButton btnEdit;
	private SeekBar mSeekbar;


	/**
	 * Constructor
	 */
	public ControllerAnalogWriteView(ControllerViewManager manager, Activity context, int controllerId,
									 String name, int controllerType, String arduinoPin, int data) {
		super(manager, context, controllerId, name, controllerType, arduinoPin,
				ArduinoUSBCommunicationManager.PINTYPE_ANALOG,
				ArduinoUSBCommunicationManager.COMMANDTYPE_WRITE, data);
		initializeLayout(name, arduinoPin, data);
	}


	/**
	 * Initialize the layout of the controller
	 */
	private void initializeLayout(String name, String arduinoPin, int data) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_slider_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		TextView tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		mSeekbar = (SeekBar) view.findViewById(R.id.controller_seekbar);
		btnEdit = (ImageButton) view.findViewById(R.id.btn_controllerview_edit);


		// Create listeners
		SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
				if (fromUser) {
					if (System.currentTimeMillis() - timeInMillis >= Constants.MAX_DELAY_TIME_SLIDER) {
						Log.d(TAG, "Can send data");
						ControllerAnalogWriteView.super.sendCommand(progressValue);
						timeInMillis = System.currentTimeMillis();
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				timeInMillis = System.currentTimeMillis();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO IT2-3-4 still not working properly when the seekbar is moved quickly
				ControllerAnalogWriteView.super.sendCommand(seekBar.getProgress());
			}
		};

		// Add click listener to the 3-dotted button
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				editController(btnEdit);
			}
		});


		// OnLongClickListener onLongClickListener = new OnLongClickListener() {
		// 	@Override
		// 	public boolean onLongClick(View view) {
		// 		switch (view.getId()) {
		// 			case R.id.card_view_main_layout:
		// 				// TODO IT2 edit cardView
		// 				ControllerSliderView.super.editController();
		// 				return true;
		// 			default:
		// 				break;
		// 		}
		// 		return false;
		// 	}
		// };

		// Modify layout
		mSeekbar.setProgress(data);
		mSeekbar.setEnabled(false);
		mSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		nameTextView.setText(name);
		tvPinNumber.setText("Pin : " + arduinoPin);
	}

	@Override
	void refreshController(String data, String units) {
		final String finalData = data;
		fromContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateNameTextView(finalData);
			}
		});
	}

	@Override
	public int getControllerData() {
		return mSeekbar.getProgress();
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void updateNameTextView(String newName) {
		nameTextView.setText(newName);
	}

	@Override
	public void startController() {
		mSeekbar.setEnabled(true);
	}

	@Override
	public void endController() {
		mSeekbar.setEnabled(false);
	}

}

