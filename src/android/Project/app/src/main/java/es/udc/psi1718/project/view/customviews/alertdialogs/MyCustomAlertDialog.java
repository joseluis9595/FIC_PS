package es.udc.psi1718.project.view.customviews.alertdialogs;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.util.Util;

// TODO finish implementation
public class MyCustomAlertDialog extends RelativeLayout {
	private MyCustomAlertDialog thisDialog = this;
	private static final String TAG = "MyCustomalertDialog";
	private Context context;

	// Layout variables
	private TextView tvTitle;
	private LinearLayout fadeLayout;
	private LinearLayout mainLayout;
	private DisplayMetrics mDisplayMetrics;
	private FloatingActionButton fab;
	private Float initialFabX, initialFabY;
	private int finalFabX, finalFabY = 0;
	private final Float fadeAlpha = 0.8f;
	private boolean isCustomAlertDialogOpened;
	private Button btnAccept;
	private Button btnCancel;
	private RelativeLayout containerLayout;
	private View customView;


	public MyCustomAlertDialog(Context context) {
		super(context);
		this.context = context;
		init(context, null);
	}

	public MyCustomAlertDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context, attrs);
	}

	public MyCustomAlertDialog(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init(context, attrs);
	}


	/**
	 * Initialize layout
	 *
	 * @param context context
	 * @param attrs   attributes
	 */
	private void init(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.customalertdialog_layout, this, true);

		// Initialize display metrics
		mDisplayMetrics = getResources().getDisplayMetrics();

		// Initialize layout variables
		tvTitle = (TextView) view.findViewById(R.id.tv_customalertdialog_title);
		fadeLayout = (LinearLayout) view.findViewById(R.id.customalertdialog_layout_fade);
		mainLayout = (LinearLayout) view.findViewById(R.id.customalertdialog_layout_newpanel);
		containerLayout = (RelativeLayout) view.findViewById(R.id.customalertdialog_layout_container);
		fab = (FloatingActionButton) view.findViewById(R.id.fab_customalerdialog);
		fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				thisDialog.show();
			}
		});

		// Buttons
		btnAccept = (Button) view.findViewById(R.id.btn_customalertdialog_create);
		btnCancel = (Button) view.findViewById(R.id.btn_customalertdialog_cancel);

		// Default onclick listener
		OnClickListener defaultListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				thisDialog.cancel();
			}
		};

		// Set default listener to the buttons
		btnAccept.setOnClickListener(defaultListener);
		btnCancel.setOnClickListener(defaultListener);

		// Add animation to the fab button
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_grow_anim);
		fab.startAnimation(animation);

	}

	/**
	 * Changes the title of the dialog
	 *
	 * @param title new title
	 */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}


	/**
	 * Changes the view of the dialog
	 *
	 * @param view new view
	 */
	public void setView(View view) {
		// Check if it has custom view or not
		customView = view;
		if (customView != null) {
			Log.e(TAG, "Is not null");
			containerLayout.removeAllViews();
			containerLayout.addView(customView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		} else {
			Log.e(TAG, "Is null");
			containerLayout.setVisibility(View.GONE);
		}
	}


	/**
	 * Set on click listener for the positive button
	 *
	 * @param onClickListener new onClickListener
	 */
	public void setPositiveClickListener(OnClickListener onClickListener) {
		btnAccept.setOnClickListener(onClickListener);
	}

	/**
	 * Set on click listener for the negative button
	 *
	 * @param onClickListener new onClickListener
	 */
	public void setNegativeClickListener(OnClickListener onClickListener) {
		btnCancel.setOnClickListener(onClickListener);
	}


	/**
	 * Check if the dialog is opened or not
	 *
	 * @return boolean
	 */
	public boolean isOpened() {
		return isCustomAlertDialogOpened;
	}

	// private void resetView(ViewGroup view) {
	// 	ArrayList<View> views = new ArrayList<>();
	//
	// 	// Get views
	// 	for (int i = 0; i < view.getChildCount(); i++) {
	// 		views.add(view.getChildAt(i));
	// 	}
	//
	// 	// Reset views
	// 	for (View element : views) {
	// 		Log.e(TAG, "Una view");
	// 		if (element instanceof AppCompatEditText) {
	// 			((AppCompatEditText) element).setText("");
	// 			Log.e(TAG, "TextInput");
	// 		} else if (element instanceof EditText) {
	// 			((EditText) element).setText("");
	// 			Log.e(TAG, "EditText");
	// 		} else if (element instanceof CheckBox) {
	// 			((CheckBox) element).setChecked(false);
	// 			Log.e(TAG, "Checkbox");
	// 		} else if (element instanceof RelativeLayout) {
	// 			Log.e(TAG, "Relative");
	// 			resetView((RelativeLayout) element);
	// 		} else if (element instanceof LinearLayout) {
	// 			Log.e(TAG, "Linear");
	// 			resetView((LinearLayout) element);
	// 		} else if (element instanceof ScrollView) {
	// 			Log.e(TAG, "Scroll");
	// 			resetView((ScrollView) element);
	// 		} else {
	// 			Log.e(TAG, "Es otra cosa mariposa");
	// 		}
	// 	}
	// }

	/**
	 * Displays the dialog
	 */
	public void show() {
		// Enable the buttons
		btnAccept.setEnabled(true);
		btnCancel.setEnabled(true);

		// Initialize measurement variables
		if (finalFabY == 0) {
			initialFabX = fab.getX();
			initialFabY = fab.getY();
			finalFabX = (mDisplayMetrics.widthPixels / 2) - fab.getWidth() / 2;
			finalFabY = (int) (initialFabY - fab.getHeight() * 2);

			Log.e(TAG, "x : " + initialFabX);
			Log.e(TAG, "y : " + initialFabY);
			Log.e(TAG, "Final x : " + finalFabX);
			Log.e(TAG, "Final y : " + finalFabY);
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			// Move fab to the new position
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Move fab to the new position
					fab.animate()
							.x(finalFabX)
							.y(finalFabY)
							.setDuration(300)
							.start();
				}
			}, 50);

			// Inflate the new layout and add fade to the screen
			new Handler().postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					if (!isCustomAlertDialogOpened) return;
					// Add fade to the background
					fadeLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							thisDialog.cancel();
						}
					});
					fadeLayout.setClickable(true);
					fadeLayout.setFocusable(true);
					fadeLayout.setAlpha(0f);
					fadeLayout.setVisibility(View.VISIBLE);
					fadeLayout.animate()
							.alpha(fadeAlpha)
							.setDuration(300)
							.start();

					// Set visibility of the whole panel to VISIBLE
					mainLayout.setVisibility(View.VISIBLE);

					// Animate with a circular material transition
					Animator a = ViewAnimationUtils.createCircularReveal(
							mainLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - mainLayout.getY()) + fab.getSize() / 2,
							fab.getSize() / 2,
							mainLayout.getHeight() * 2f);

					a.start();

				}
			}, 350);
		} else {
			// Add fade to the background
			fadeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					thisDialog.cancel();
				}
			});
			fadeLayout.setClickable(true);
			fadeLayout.setFocusable(true);
			fadeLayout.setAlpha(0f);
			fadeLayout.setVisibility(View.VISIBLE);
			fadeLayout.animate()
					.alpha(fadeAlpha)
					.setDuration(300)
					.start();
			// Show the main layout
			mainLayout.setVisibility(View.VISIBLE);
		}

		isCustomAlertDialogOpened = true;
	}


	/**
	 * Closes the dialog
	 */
	public void cancel() {
		// Hide the keyboard
		Util.hideKeyboard((Activity) context);

		// Disable the buttons
		btnAccept.setEnabled(false);
		btnCancel.setEnabled(false);

		// Hide layout and fade
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Remove fade from the background
				fadeLayout.setClickable(false);
				fadeLayout.setFocusable(false);
				fadeLayout.animate()
						.alpha(0f)
						.setDuration(300)
						.start();

				// Set visibility of the whole panel to INVISIBLE
				// newPanelLayout.setVisibility(View.VISIBLE);

				// If API > 21 animate with a circular material transition
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Animator a = ViewAnimationUtils.createCircularReveal(
							mainLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - mainLayout.getY()) + fab.getSize() / 2,
							mainLayout.getHeight() * 2f,
							fab.getSize() / 2);
					a.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							mainLayout.setVisibility(View.INVISIBLE);
						}
					});

					a.start();
				} else {
					mainLayout.setVisibility(View.INVISIBLE);
				}
			}
		}, 50);

		// Move fab back to its position
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Move fab to the new position
				fab.animate()
						.x(initialFabX)
						.y(initialFabY)
						.setDuration(300)
						.start();
			}
		}, 350);

		// Clear the edit text
		isCustomAlertDialogOpened = false;

		// // Reset the view
		// resetView(containerLayout);
	}
}
















