package es.udc.psi1718.project.view.customviews.alertdialogs;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import es.udc.psi1718.project.R;

// TODO finish implementation
public class MyCustomAlertDialog extends RelativeLayout {
	public MyCustomAlertDialog(Context context) {
		super(context);
	}

	public MyCustomAlertDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCustomAlertDialog(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.customalertdialog_layout, this, true);


	}
}
