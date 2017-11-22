package es.udc.psi1718.project.ui.customviews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.ui.customviews.controllers.ControllerView;


/**
 * Each item of our {@link ControllersGridLayout} custom grid layout
 */
public class ControllersGridItem extends CardView {
	private static final String TAG = "CustomGridItem";

	private ControllerView controllerView;
	private Context context;

	public ControllersGridItem(Context context, ControllerView controllerView) {
		super(context);
		this.controllerView = controllerView;
		initializeLayout(context);
	}


	/**
	 * Initialize the layout
	 *
	 * @param context context
	 */
	private void initializeLayout(Context context) {
		this.context = context;

		// Inflate the layout of the item
		View itemView = LayoutInflater.from(context).inflate(R.layout.customgrid_items, this);
		LinearLayout containerLayout = (LinearLayout) itemView.findViewById(R.id.customgrid_container_layout);

		// Add the view of the controller to our item
		containerLayout.addView(controllerView.getView());
	}


	/**
	 * Change the position of the item within its layout
	 *
	 * @param position position of the item
	 */
	public void setPosition(int position) {
		controllerView.setPosition(position);
	}
}
