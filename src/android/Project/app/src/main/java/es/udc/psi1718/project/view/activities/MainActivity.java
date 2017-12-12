package es.udc.psi1718.project.view.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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
    private boolean isCustomAlertDialogOpened;

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
    public void onBackPressed() {
        if (isCustomAlertDialogOpened) {
            closeCustomAlertDialog();
            return;
        }
        super.onBackPressed();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo mInfo) {
        super.onCreateContextMenu(menu, v, mInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        Cursor cursor = (Cursor) lvPannels.getItemAtPosition(listPosition);
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_ID));

        switch (item.getItemId()) {
            case R.id.delete:
                inflateConfirmationDialog(id);
                return true;
            case R.id.update:
                // TODO inflate update dialog
                return true;
            default:
                return super.onContextItemSelected(item);
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
                openCustomAlertDialog();
            }
        });

        // Add listener to the listView
        lvPannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long cursorID) {
                Log.d(TAG, "Pressed item " + i + " on the listview, cursorID : " + cursorID);
                // Intents
                controllersIntent = new Intent(context, ControllersActivity.class);
                controllersIntent.putExtra(Constants.INTENTCOMM_PANELID, (int) cursorID);

                // TODO mejorar esto, poca independencia con base de datos
                Cursor selectedFromList = (Cursor) lvPannels.getItemAtPosition(i);
                String panelName = selectedFromList.getString(selectedFromList.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_NAME));
                controllersIntent.putExtra(Constants.INTENTCOMM_PANELNAME, panelName);
                controllersIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add shared components animation for a more fluid UX
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // Shared components we'll add to the animation
                    View backgroundLayout = view.findViewById(R.id.panelitem_background);
                    View statusBar = findViewById(android.R.id.statusBarBackground);
                    View navigationBar = findViewById(android.R.id.navigationBarBackground);

                    List<Pair<View, String>> pairs = new ArrayList<>();
                    if (navigationBar != null) {
                        pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                    }
                    pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                    pairs.add(Pair.create(backgroundLayout, backgroundLayout.getTransitionName()));
                    pairs.add(Pair.create((View) myToolbar, myToolbar.getTransitionName()));

                    // Create the bundle with previous options
                    Bundle optionsBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                            pairs.toArray(new Pair[pairs.size()])).toBundle();

                    // Start activity
                    startActivity(controllersIntent, optionsBundle);
                } else {
                    startActivity(controllersIntent);
                }
            }
        });

        // Initialize listView with panels information
        refreshListView();

        registerForContextMenu(lvPannels);

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

    public void deletePanel(int id) {
        mySQLiteHelper.deletePanel(id);
        refreshListView();
    }

    /**
     * Refresh the panel's listView
     */
    private void refreshListView() {
        Cursor cursor = mySQLiteHelper.getAllPanels();
        lvPannels.setAdapter(new PanelCursorAdapter(context, cursor));

        // TODO debug
        // String cursorString = DatabaseUtils.dumpCursorToString(cursor);
        // Log.e(TAG, cursorString);
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

    private void inflateConfirmationDialog(final int idPanel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.alertdialog_title_deletepanel)
                .setMessage(R.string.alertdialog_message_deletepanel);

        builder.setPositiveButton(R.string.alertdialog_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePanel(idPanel);
            }
        });
        builder.setNegativeButton(R.string.alertdialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Shows a custom alert dialog for creating a new panel
     */
    private void openCustomAlertDialog() {
        // Initialize measurement variables
        if (finalFabY == 0) {
            initialFabX = fabNewPanel.getX();
            initialFabY = fabNewPanel.getY();
            finalFabX = (mDisplayMetrics.widthPixels / 2) - fabNewPanel.getWidth() / 2;
            finalFabY = (int) (initialFabY - fabNewPanel.getHeight() * 2);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Move fab to the new position
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
                            closeCustomAlertDialog();
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
                    newPanelLayout.setVisibility(View.VISIBLE);

                    // Animate with a circular material transition
                    Animator a = ViewAnimationUtils.createCircularReveal(
                            newPanelLayout,
                            mDisplayMetrics.widthPixels / 2,
                            (int) (finalFabY - newPanelLayout.getY()) + fabNewPanel.getSize() / 2,
                            fabNewPanel.getSize() / 2,
                            newPanelLayout.getHeight() * 2f);

                    a.start();

                }
            }, 350);
        } else {
            // Add fade to the background
            fadeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeCustomAlertDialog();
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
            newPanelLayout.setVisibility(View.VISIBLE);
        }

        isCustomAlertDialogOpened = true;
    }


    /**
     * Closes the custom alertDialog
     */
    private void closeCustomAlertDialog() {
        // Hide the keyboard
        Util.hideKeyboard(activity);

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
                } else {
                    newPanelLayout.setVisibility(View.INVISIBLE);
                }
            }
        }, 50);

        // Move fab back to its position
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

        // Clear the edit text
        etNewPanelName.setText("");
        isCustomAlertDialogOpened = false;
    }
}















