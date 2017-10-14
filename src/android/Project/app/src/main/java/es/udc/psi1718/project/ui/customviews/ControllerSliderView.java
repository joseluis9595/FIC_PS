package es.udc.psi1718.project.ui.customviews;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import es.udc.psi1718.project.R;

/**
 * Created by jose on 14/10/17.
 */

public class ControllerSliderView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	private View view;
	private TextView nameTextView;
	private SeekBar mSeekbar;
	private LinearLayout cardViewLayout;


	public ControllerSliderView(Context context, String name, String arduinoPin, String pinType, String dataType) {
		super(context, name, arduinoPin, pinType, dataType);
		initializeLayout(name, arduinoPin, pinType, dataType);
	}


	private void initializeLayout(String name, String arduinoPin, String pinType, String dataType) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_slider_layout, null);

		// Initialize variables
		cardViewLayout = (LinearLayout) view.findViewById(R.id.card_view_main_layout);
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		mSeekbar = (SeekBar) view.findViewById(R.id.controller_seekbar);

		// Create listeners
		SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
				if (b) {
					Log.d(TAG, "OnSeekbarChange() : " + progressValue);
				} else {
					Log.d(TAG, "OnSeekbarChange() : Not from user");
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO start a timer here, and every x milliseconds, allow onProgressChange to send command
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				ControllerSliderView.super.controllerChangedState(seekBar.getProgress());
			}
		};


		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.card_view_main_layout:
						// TODO edit cardView
						ControllerSliderView.super.editController();
						break;
					default:
						break;
				}
			}
		};

		// Modify layout
		cardViewLayout.setOnClickListener(onClickListener);
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

}

