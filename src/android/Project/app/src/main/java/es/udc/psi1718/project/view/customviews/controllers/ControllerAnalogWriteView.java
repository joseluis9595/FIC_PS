package es.udc.psi1718.project.view.customviews.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.util.Constants;


public class ControllerAnalogWriteView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	// Layout variables
	private View view;
	private TextView nameTextView;

	// Temporal fix for sliders
	private long timeInMillis;


	/**
	 * Constructor
	 */
	public ControllerAnalogWriteView(Context context, int controllerId, String name, int controllerType, String arduinoPin) {
		super(context, controllerId, name, controllerType, arduinoPin,
				ArduinoCommunicationManager.PINTYPE_ANALOG,
				ArduinoCommunicationManager.COMMANDTYPE_WRITE);
		initializeLayout(name, arduinoPin);
	}


	/**
	 * Initialize the layout of the controller
	 */
	private void initializeLayout(String name, String arduinoPin) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_slider_layout, null);

		// Initialize variables
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		TextView tvPinNumber = (TextView) view.findViewById(R.id.tv_controller_position);
		SeekBar mSeekbar = (SeekBar) view.findViewById(R.id.controller_seekbar);

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
		// cardViewLayout.setOnLongClickListener(onLongClickListener);
		mSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		nameTextView.setText(name);
		// Change view
		tvPinNumber.setText("Pin : " + arduinoPin);
	}

	@Override
	void receivedData(String data) {
		// setName(data);
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void setName(String newName) {
		nameTextView.setText(newName);
	}

}

