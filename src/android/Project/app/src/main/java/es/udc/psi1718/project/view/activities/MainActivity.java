package es.udc.psi1718.project.view.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.MySharedPrefsManager;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;
import es.udc.psi1718.project.storage.database.daos.Panel;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;
import es.udc.psi1718.project.view.customviews.pannelslist.PanelCursorAdapter;

public class MainActivity extends AppCompatActivity {

	public static Boolean active = false;
	private Context context = this;
	private Activity activity = this;
	private String TAG = "MainActivity";

	// Intents
	private int SETTINGSACTIV_REQUESTCODE = 2;
	private Intent controllersIntent;

	// Database helper
	private MySQLiteHelper mySQLiteHelper;

	// Custom Alert Dialog
	private DisplayMetrics mDisplayMetrics;
	private Float initialFabX, initialFabY;
	private int finalFabX, finalFabY = 0;
	private final Float fadeAlpha = 0.8f;

	// Layout variables
	private ListView lvPannels;
	private FloatingActionButton fabNewPanel;
	private Toolbar myToolbar;
	private LinearLayout fadeLayout;
	private LinearLayout newPanelLayout;
	private EditText etNewPanelName;
	private Button btnCancel, btnCreate;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change theme of the activity according to user's preferences
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");

		// Add the toolbar
		myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// If first time opening the app, disable light-icon activity-alias
		if (MySharedPrefsManager.getInstance(context).isFirstTimeOpening()) {
			Util.disableComponent(getPackageManager(), new ComponentName(this, "es.udc.psi1718.project.LightIcon"));
		}

		// Show tutorial if necessary
		if (MySharedPrefsManager.getInstance(context).shouldShowTutorial()) {
			startTutorial(true);
		}

		// Database helper
		mySQLiteHelper = new MySQLiteHelper(this);

		// Intents
		controllersIntent = new Intent(context, ControllersActivity.class);

		// Initialize layout
		initializeLayout();
	}


	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		active = true;

		// Add animation to the fab
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_grow_anim);
		fabNewPanel.startAnimation(animation);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult");
		if (requestCode == SETTINGSACTIV_REQUESTCODE) {
			if (resultCode == 0) {
				this.recreate();
			} else if (resultCode == Constants.INTENTCOMM_DONT_RECREATE) {
				this.finish();
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		active = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				Intent aboutIntent = new Intent(this, AboutActivity.class);
				startActivity(aboutIntent);
				return true;
			case R.id.action_settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, SETTINGSACTIV_REQUESTCODE);
				return true;
			case R.id.action_tutorial:
				startTutorial(false);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Function to initialize all layout variables
	 */
	private void initializeLayout() {
		lvPannels = (ListView) findViewById(R.id.lv_mainactiv);
		fabNewPanel = (FloatingActionButton) findViewById(R.id.fab_mainactiv_newpanel);

		// Add listener to the fab
		fabNewPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "fab pressed");
				// createNewPanelDialog();

				myTestFunction();

			}
		});

		// Add listener to the listView
		lvPannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long cursorID) {
				Log.d(TAG, "Pressed item " + i + " on the listview, cursorID : " + cursorID);
				controllersIntent.putExtra(Constants.INTENTCOMM_PANELID, (int) cursorID);

				// TODO mejorar esto, poca independencia con base de datos
				Cursor selectedFromList = (Cursor) lvPannels.getItemAtPosition(i);
				String panelName = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_NAME));
				controllersIntent.putExtra(Constants.INTENTCOMM_PANELNAME, panelName);

				// TODO buscar más componentes compartidos entre actividades
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					View backgroundLayout = view.findViewById(R.id.panelitem_background);
					// final ActivityOptionsCompat options = ActivityOptionsCompat.
					// 		makeSceneTransitionAnimation(activity, backgroundLayout, "panelitem_background");
					//
					// startActivity(controllersIntent, options.toBundle());

					View statusBar = findViewById(android.R.id.statusBarBackground);
					View navigationBar = findViewById(android.R.id.navigationBarBackground);

					List<Pair<View, String>> pairs = new ArrayList<>();
					if (navigationBar != null) {
						pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
					}
					pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
					pairs.add(Pair.create(backgroundLayout, backgroundLayout.getTransitionName()));
					pairs.add(Pair.create((View) myToolbar, myToolbar.getTransitionName()));

					// ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
					// 		Pair.create(backgroundLayout, backgroundLayout.getTransitionName()),
					// 		Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME),
					// 		Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

					Bundle optionsBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
							pairs.toArray(new Pair[pairs.size()])).toBundle();

					startActivity(controllersIntent, optionsBundle);

				} else {
					startActivity(controllersIntent);
				}


			}
		});

		// Initialize listView with pannels information
		refreshListView();

		// Initialize custom alertDialog layout
		initializeCustomAlertDialogLayout();
	}

	/**
	 * Initializes variables and layout from the custom alertDialog
	 */
	private void initializeCustomAlertDialogLayout() {
		mDisplayMetrics = getResources().getDisplayMetrics();
		fadeLayout = (LinearLayout) findViewById(R.id.mainactiv_layout_fade);
		newPanelLayout = (LinearLayout) findViewById(R.id.mainactiv_layout_newpanel);
		etNewPanelName = (EditText) findViewById(R.id.et_newpanel_name);
		btnCancel = (Button) findViewById(R.id.btn_newpanel_cancel);
		btnCreate = (Button) findViewById(R.id.btn_newpanel_create);

		// Create one common listener for the buttons
		View.OnClickListener buttonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.btn_newpanel_cancel:
						closeCustomAlertDialog();
						break;
					case R.id.btn_newpanel_create:
						if (Util.isEmptyEditText(etNewPanelName)) {
							// Don't dismiss the dialog
							Util.displayMessage(context, getString(R.string.err_completeallfields));
						} else {
							// Create a new controller
							mySQLiteHelper.insertPanel(new Panel(etNewPanelName.getText().toString()));
							refreshListView();
							closeCustomAlertDialog();
						}
						break;
					default:
						break;
				}
			}
		};

		// Add listener to the views
		btnCancel.setOnClickListener(buttonClickListener);
		btnCreate.setOnClickListener(buttonClickListener);
	}


	/**
	 * Refresh the panel's listView
	 */
	private void refreshListView() {
		Cursor cursor = mySQLiteHelper.getAllPanels();
		lvPannels.setAdapter(new PanelCursorAdapter(context, cursor));

		// TODO debug
		String cursorString = DatabaseUtils.dumpCursorToString(cursor);
		Log.e(TAG, cursorString);
	}

	/**
	 * Inflates an Alert Dialog to create a new panel
	 */
	private void createNewPanelDialog() {
		// Create AlertDialog builder
		// final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustomTheme);
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		// Inflate and set the custom view
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.alertdialog_newpanel_layout, null);
		dialogBuilder.setView(dialogView);

		// Save the views inside the alertDialog
		final EditText etPanelName = (EditText) dialogView.findViewById(R.id.et_newpanel_name);

		// Set the rest of the options
		dialogBuilder
				.setTitle(R.string.alertdialog_newpanel_title)
				.setCancelable(false)
				.setPositiveButton("Create", null)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});

		// Display AlertDialog
		final AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();

		// Add animation to the view inside the alertDialog
		dialogView.startAnimation(AnimationUtils.loadAnimation(
				context, android.R.anim.slide_in_left));

		// Change opacity of negative button
		alertDialog
				.getButton(AlertDialog.BUTTON_NEGATIVE)
				.setAlpha(0.7f);

		// Override onClickListener so that we can control when alertDialog closes
		alertDialog
				.getButton(AlertDialog.BUTTON_POSITIVE)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String controllerNameString = etPanelName.getText().toString();
						if (Util.isEmptyEditText(etPanelName)) {
							// Don't dismiss the dialog
							Util.displayMessage(context, getString(R.string.err_completeallfields));
						} else {
							// Create a new controller
							mySQLiteHelper.insertPanel(new Panel(etPanelName.getText().toString()));
							refreshListView();
							alertDialog.dismiss();
						}

					}
				});
	}

	/**
	 * Starts the tutorial activity and finished this one
	 */
	private void startTutorial(Boolean finishActivity) {
		Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
		startActivity(tutorialIntent);
		if (finishActivity)
			this.finish();
	}


	/**
	 * Shows a custom alert dialog for creating a new panel
	 */
	private void myTestFunction() {
		if (finalFabY == 0) {
			// Initialize measurement variables
			initialFabX = fabNewPanel.getX();
			initialFabY = fabNewPanel.getY();
			finalFabX = (mDisplayMetrics.widthPixels / 2) - fabNewPanel.getWidth() / 2;
			Log.e(TAG, "Value is : " + fabNewPanel.getWidth());
			Log.e(TAG, "Screen is : " + mDisplayMetrics.widthPixels);
			finalFabY = (int) (initialFabY - fabNewPanel.getHeight() * 2);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Move fab to the new position
				fabNewPanel.animate()
						.x(finalFabX)
						.y(finalFabY)
						.setDuration(300)
						.start();
			}
		}, 50);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Add fade to the background
				fadeLayout.setAlpha(0f);
				fadeLayout.setVisibility(View.VISIBLE);
				fadeLayout.animate()
						.alpha(fadeAlpha)
						.setDuration(300)
						.start();

				// Set visibility of the whole panel to VISIBLE
				newPanelLayout.setVisibility(View.VISIBLE);

				// If API > 21 animate with a circular material transition
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Animator a = ViewAnimationUtils.createCircularReveal(
							newPanelLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
							fabNewPanel.getSize() / 2,
							newPanelLayout.getHeight() * 2f);

					a.start();
				}
			}
		}, 350);


		// 				// Add fade to the background
		// 				fadeLayout.setAlpha(0f);
		// 				fadeLayout.setVisibility(View.VISIBLE);
		// 				fadeLayout.animate()
		// 						.alpha(0.8f)
		// 						.setDuration(300)
		// 						.start();
		//
		// 				// Set visibility of the whole panel to VISIBLE
		// 				newPanelLayout.setVisibility(View.VISIBLE);
		//
		// 				// If API > 21 animate with a circular material transition
		// 				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// 					Animator a = ViewAnimationUtils.createCircularReveal(
		// 							newPanelLayout,
		// 							finalFabX,
		// 							(int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
		// 							fabNewPanel.getSize() / 2,
		// 							newPanelLayout.getHeight() * 2f);
		//
		// 					a.start();


		// Move fab to the new position
		// fabNewPanel.animate()
		// 		.x(finalFabX)
		// 		.y(finalFabY)
		// 		.setDuration(300)
		// 		.setListener(new AnimatorListenerAdapter() {
		// 			@Override
		// 			public void onAnimationEnd(Animator animation) {
		// 				super.onAnimationEnd(animation);
		//
		// 				// TODO hide fab
		//
		// 				// Add fade to the background
		// 				fadeLayout.setAlpha(0f);
		// 				fadeLayout.setVisibility(View.VISIBLE);
		// 				fadeLayout.animate()
		// 						.alpha(0.8f)
		// 						.setDuration(300)
		// 						.start();
		//
		// 				// Set visibility of the whole panel to VISIBLE
		// 				newPanelLayout.setVisibility(View.VISIBLE);
		//
		// 				// If API > 21 animate with a circular material transition
		// 				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// 					Animator a = ViewAnimationUtils.createCircularReveal(
		// 							newPanelLayout,
		// 							finalFabX,
		// 							(int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
		// 							fabNewPanel.getSize() / 2,
		// 							newPanelLayout.getHeight() * 2f);
		//
		// 					a.start();
		// 				}
		// 			}
		// 		})
		// 		.start();

		// final Float mStartX = fabNewPanel.getX();
		// final Float mStartY = fabNewPanel.getY();
		// int marginBottom = (int) mDisplayMetrics.density;
		// final int x = mDisplayMetrics.widthPixels / 2 - fabNewPanel.getSize() / 2;
		// final int y = mDisplayMetrics.heightPixels - marginBottom * 250 + fabNewPanel.getSize() / 2;
		//
		// // Initialize the animator variable
		// ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
		// animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		// 	@Override
		// 	public void onAnimationUpdate(ValueAnimator animation) {
		// 		float v = (float) animation.getAnimatedValue();
		//
		// 		fabNewPanel.setX(
		// 				x + (mStartX - x - ((mStartX - x) * v))
		// 		);
		//
		// 		fabNewPanel.setY(
		// 				y + (mStartY - y - ((mStartY - y) * (v * v)))
		// 		);
		// 	}
		// });
		//
		// // Add animationEnd listener
		// animator.addListener(new AnimatorListenerAdapter() {
		// 	@Override
		// 	public void onAnimationEnd(Animator animation) {
		// 		super.onAnimationEnd(animation);
		//
		// 		// Add fade to the background
		// 		fadeLayout.setAlpha(0f);
		// 		fadeLayout.setVisibility(View.VISIBLE);
		// 		fadeLayout.animate()
		// 				.alpha(0.8f)
		// 				.setDuration(300)
		// 				.start();
		//
		// 		// Hide fab
		// 		fabNewPanel.setVisibility(View.INVISIBLE);
		//
		// 		// Set visibility of the whole panel to VISIBLE
		// 		newPanelLayout.setVisibility(View.VISIBLE);
		//
		// 		// If API > 21 animate with a circular material transition
		// 		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// 			Animator a = ViewAnimationUtils.createCircularReveal(
		// 					newPanelLayout,
		// 					mDisplayMetrics.widthPixels / 2,
		// 					(int) (y - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
		// 					fabNewPanel.getSize() / 2,
		// 					newPanelLayout.getHeight() * 2f);
		//
		// 			a.start();
		// 		}
		//
		//
		// 	}
		// });
		//
		// // Start animation
		// animator.start();
	}


	private void closeCustomAlertDialog() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Remove fade from the background
				fadeLayout.animate()
						.alpha(0f)
						.setDuration(300)
						.start();

				// Set visibility of the whole panel to INVISIBLE
				// newPanelLayout.setVisibility(View.VISIBLE);

				// If API > 21 animate with a circular material transition
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Animator a = ViewAnimationUtils.createCircularReveal(
							newPanelLayout,
							mDisplayMetrics.widthPixels / 2,
							(int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
							newPanelLayout.getHeight() * 2f,
							fabNewPanel.getSize() / 2);
					a.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							newPanelLayout.setVisibility(View.INVISIBLE);
						}
					});

					a.start();
				}
			}
		}, 50);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Move fab to the new position
				fabNewPanel.animate()
						.x(initialFabX)
						.y(initialFabY)
						.setDuration(300)
						.start();
			}
		}, 350);

		// Hide the keyboard
		Util.hideKeyboard(activity);


		// int marginBottom = (int) mDisplayMetrics.density;
		// final int fabStartX = (int) fabNewPanel.getX();
		// final int fabStartY = (int) fabNewPanel.getY();
		// final int fabFinalX = mDisplayMetrics.widthPixels / 2 - fabNewPanel.getSize() / 2;
		// final int fabFinalY = mDisplayMetrics.heightPixels - marginBottom * 250 + fabNewPanel.getSize() / 2;
		//
		// Hide fade layout
		// fadeLayout.animate()
		// 		.alpha(0f)
		// 		.setDuration(300)
		// 		.setListener(new AnimatorListenerAdapter() {
		// 			@Override
		// 			public void onAnimationEnd(Animator animation) {
		// 				super.onAnimationEnd(animation);
		// 				Log.e(TAG, "Entra en el bucle");
		// 				fadeLayout.setVisibility(View.GONE);
		//
		// 				// If API > 21 animate with a circular material transition
		// 				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// 					Animator a = ViewAnimationUtils.createCircularReveal(
		// 							newPanelLayout,
		// 							mDisplayMetrics.widthPixels / 2,
		// 							(int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
		// 							newPanelLayout.getHeight() * 2f,
		// 							fabNewPanel.getSize() / 2);
		//
		// 					a.addListener(new AnimatorListenerAdapter() {
		// 						@Override
		// 						public void onAnimationEnd(Animator animation) {
		// 							super.onAnimationEnd(animation);
		// 							// Set visibility of the whole panel to INVISIBLE
		// 							newPanelLayout.setVisibility(View.GONE);
		// 						}
		// 					});
		//
		// 					// Start animation
		// 					a.start();
		// 				} else {
		// 					// Set visibility of the whole panel to INVISIBLE
		// 					newPanelLayout.setVisibility(View.GONE);
		// 				}
		// 			}
		// 		})
		// 		.start();

		// new Handler().postDelayed(new Runnable() {
		// 	@Override
		// 	public void run() {
		// 		fabNewPanel.animate()
		// 				.x(initialFabX)
		// 				.y(initialFabY)
		// 				.setDuration(300)
		// 				.start();
		//
		// 	}
		// }, 350);

		//
		// // TODO Change fab visibility
		// // TODO Move fab to its original position
		//
		// ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
		// animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		// 	@Override
		// 	public void onAnimationUpdate(ValueAnimator animation) {
		// 		float v = (float) animation.getAnimatedValue();
		// 		fabNewPanel.setX(fabFinalX + ((fabStartX - fabFinalX) * v));
		// 		fabNewPanel.setY((float) (fabFinalY + (fabStartY - fabFinalY) * (Math.pow(v, .5f))));
		// 	}
		// });
		// animator.start();

	}
}















