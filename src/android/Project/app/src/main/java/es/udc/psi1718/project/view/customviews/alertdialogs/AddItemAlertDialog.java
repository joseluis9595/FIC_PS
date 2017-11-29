package es.udc.psi1718.project.view.customviews.alertdialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

// TODO implement this class
public class AddItemAlertDialog extends AlertDialog {

	private View view;
	private Boolean useAnimation;

	private AddItemAlertDialog(@NonNull Context context, View view, Boolean useAnimation) {
		super(context);
		this.view = view;
		this.useAnimation = useAnimation;
	}

}
