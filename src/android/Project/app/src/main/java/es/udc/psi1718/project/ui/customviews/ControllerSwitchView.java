package es.udc.psi1718.project.ui.customviews;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import es.udc.psi1718.project.R;


public class ControllerSwitchView extends ControllerView {

	private String TAG = "ControllerSwitchView";

	private View view;
	private TextView nameTextView;
	private Switch mSwitch;
	private LinearLayout cardViewLayout;


	public ControllerSwitchView(Context context, String name, String arduinoPin, String pinType, String dataType) {
		super(context, name, arduinoPin, pinType, dataType);
		initializeLayout(name, arduinoPin, pinType, dataType);
	}


	private void initializeLayout(String name, String arduinoPin, String pinType, String dataType) {
		// Inflate view
		view = inflate(getContext(), R.layout.controller_layout, null);

		// Initialize variables
		cardViewLayout = (LinearLayout) view.findViewById(R.id.card_view_main_layout);
		nameTextView = (TextView) view.findViewById(R.id.controller_name_text_view);
		mSwitch = (Switch) view.findViewById(R.id.controller_switch);

		// Create listeners
		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				int data = (b) ? 1 : 0;
				Log.d(TAG, "OnCheckedChange()");
				ControllerSwitchView.super.controllerChangedState(data);
			}
		};
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.card_view_main_layout:
						// TODO edit cardView
						ControllerSwitchView.super.editController();
						break;
					default:
						break;
				}
			}
		};

		// Modify layout
		cardViewLayout.setOnClickListener(onClickListener);
		mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
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
