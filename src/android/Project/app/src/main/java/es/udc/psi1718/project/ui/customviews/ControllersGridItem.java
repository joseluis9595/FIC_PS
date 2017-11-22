package es.udc.psi1718.project.ui.customviews;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.ui.customviews.controllers.ControllerView;

public class ControllersGridItem extends CardView {
	private static final String TAG = "CustomGridItem";

	private ControllerView controllerView;
	// private GridLayout parentLayout;
	private Context context;

	public ControllersGridItem(Context context, ControllerView controllerView) {
		super(context);
		// this.parentLayout = parentLayout;
		this.controllerView = controllerView;
		Log.e(TAG, "First builder");
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		View itemView = LayoutInflater.from(context).inflate(R.layout.customgrid_items, this);
		// LayoutInflater inflater = LayoutInflater.from(context);
		// final View itemView = inflater.inflate(R.layout.customgrid_items, parentLayout, false);
		LinearLayout containerLayout = (LinearLayout) itemView.findViewById(R.id.customgrid_container_layout);

		containerLayout.addView(controllerView.getView());

		// itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
		// itemView.setOnLongClickListener(new CustomGridLayout.LongPressListener());
	}

	// public CustomGridItem(Context context, ControllerView controllerView) {
	// 	this.context = context;
	// 	this.controllerView = controllerView;
	// }
	//
	// public View getView() {
	// 	LayoutInflater inflater = LayoutInflater.from(context);
	// 	final View itemView = inflater.inflate(R.layout.customgrid_items, null, false);
	// 	LinearLayout containerLayout = (LinearLayout) itemView.findViewById(R.id.customgrid_container_layout);
	// 	containerLayout.addView(controllerView.getView());
	//
	// 	// final TextView text = (TextView) itemView.findViewById(R.id.text);
	// 	// text.setText(String.valueOf(99));
	// 	//
	// 	itemView.setOnLongClickListener(new CustomGridLayout.LongPressListener());
	// 	// parentLayout.addView(itemView);
	// 	return itemView;
	// }

	public void testFunction(int position) {
		controllerView.testFunction(position);
	}
}
