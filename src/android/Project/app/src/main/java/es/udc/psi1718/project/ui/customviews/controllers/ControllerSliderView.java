package es.udc.psi1718.project.ui.customviews.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.arduinomanager.ArduinoCommunicationManager;
import es.udc.psi1718.project.util.Constants;


public class ControllerSliderView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	// Layout variables
	private View view;
	private TextView nameTextView, positionTextView;
	private SeekBar mSeekbar;
	private LinearLayout cardViewLayout;

	//
	private long timeInMillis;


	public ControllerSliderView(Context context, String name, String arduinoPin, String pinType, String commandType) {
		super(context, name, arduinoPin, pinType, commandType);
		initializeLayout(name, arduinoPin, pinType, commandType);
	}


	private void initializeLayout(String name, String arduinoPin, String pinType, String commandType) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_slider_layout, null);

		// Initialize variables
		cardViewLayout = (LinearLayout) view.findViewById(R.id.card_view_main_layout);
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		positionTextView = (TextView) view.findViewById(R.id.tv_controller_position);
		mSeekbar = (SeekBar) view.findViewById(R.id.controller_seekbar);

		// Create listeners
		SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
				if (fromUser) {
					if (System.currentTimeMillis() - timeInMillis >= Constants.MAX_DELAY_TIME_SLIDER) {
						Log.d(TAG, "Can send data");
						ControllerSliderView.super.sendCommand(
								progressValue,
								ArduinoCommunicationManager.PINTYPE_ANALOG,
								ArduinoCommunicationManager.COMMANDTYPE_WRITE);
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
				ControllerSliderView.super.sendCommand(
						seekBar.getProgress(),
						ArduinoCommunicationManager.PINTYPE_ANALOG,
						ArduinoCommunicationManager.COMMANDTYPE_WRITE);
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
	public void testFunction(int position) {
		positionTextView.setText("" + position);
	}

}

