package es.udc.psi1718.project.view.customviews.tutorial;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.udc.psi1718.project.R;


public class TutorialStepItemLayout extends LinearLayout {
	private String TAG = "TutorialStepItemLayout";


	public TutorialStepItemLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initializeLayout(context, attrs);
	}

	public TutorialStepItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initializeLayout(context, attrs);
	}


	/**
	 * Initialize the layout
	 *
	 * @param context context
	 */
	private void initializeLayout(Context context, AttributeSet attrs) {
		// Inflate the layout of the item
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.tutorial_stepitem_layout, this, true);

		// Get custom attributes
		String numberString;
		String contentString;

		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.TutorialStepItemLayout,
				0, 0);
		try {
			numberString = (String) a.getText(R.styleable.TutorialStepItemLayout_tutorialItem_number);
			contentString = (String) a.getText(R.styleable.TutorialStepItemLayout_tutorialItem_text);
		} finally {
			a.recycle();
		}

		// Modify the view
		TextView tvNumber = (TextView) view.findViewById(R.id.tv_tutorial_stepitem_number);
		TextView tvContent = (TextView) view.findViewById(R.id.tv_tutorial_stepitem_content);
		tvNumber.setText(numberString);
		tvContent.setText(contentString);
	}
}
