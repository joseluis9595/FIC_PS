package es.udc.psi1718.project.ui.customviews;


import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.ui.customviews.controllers.ControllerView;

public class CustomGridItem {
	private static final String TAG = "CustomGridItem";

	private ControllerView controllerView;
	private Context context;

	public CustomGridItem(Context context, ControllerView controllerView) {
		this.context = context;
		this.controllerView = controllerView;
	}

	public void createView(GridLayout parentLayout) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View itemView = inflater.inflate(R.layout.customgrid_items, parentLayout, false);
		LinearLayout containerLayout = (LinearLayout) itemView.findViewById(R.id.customgrid_container_layout);
		containerLayout.addView(controllerView.getView());

		// final TextView text = (TextView) itemView.findViewById(R.id.text);
		// text.setText(String.valueOf(99));
		//
		itemView.setOnLongClickListener(new CustomGridLayout.LongPressListener());
		parentLayout.addView(itemView);
	}
}
