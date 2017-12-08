package es.udc.psi1718.project.view.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.UserPreferencesManager;
import es.udc.psi1718.project.util.Constants;
import es.udc.psi1718.project.util.Util;


/**
 * Activity that displays various settings for our application
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final String TAG = "SettingsActivity";
	private Context context = this;

	// Shared Preferences variables
	private SharedPreferences userPrefs;
	private static Boolean useDarkIcon = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_settings);

		// Add the toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// Add home button in the toolbar
		try {
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// Load user preferences
		userPrefs = getSharedPreferences(getString(R.string.userprefs_filename), Context.MODE_PRIVATE);

		// Register sharedPreferencesChanged listener to userPrefs
		userPrefs.registerOnSharedPreferenceChangeListener(this);
		Log.e(TAG, "Registered userPrefs listener");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		// Unregister userPrefs' listener
		userPrefs.unregisterOnSharedPreferenceChangeListener(this);
		Log.e(TAG, "Unregistered listener");
	}

	@Override
	public void onBackPressed() {
		Log.e(TAG, "onBackPressed");
		changeAppIcon(useDarkIcon);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home) {
			changeAppIcon(useDarkIcon);
			finish();
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.e(TAG, "userPrefs changed: " + sharedPreferences.toString());
		Log.e(TAG, "key changed: " + key);
		if (key.equals(getString(R.string.userprefs_theme))) {
			Log.e(TAG, "Changed theme");
			// Recreate the activity to apply new theme
			this.recreate();
		} else if (key.equals(getString(R.string.userprefs_icon))) {
			useDarkIcon = sharedPreferences.getBoolean(key, true);
		}
	}

	/**
	 * Changes the app icon
	 *
	 * @param useDarkIcon boolean
	 */
	private void changeAppIcon(Boolean useDarkIcon) {
		Log.d(TAG, "useDarkIcon is " + useDarkIcon);
		if (useDarkIcon == null) return;
		ComponentName darkIconComponent = new ComponentName(this, "es.udc.psi1718.project.DarkIcon");
		ComponentName lightIconComponent = new ComponentName(this, "es.udc.psi1718.project.LightIcon");

		PackageManager packageManager = getPackageManager();
		if (useDarkIcon) {
			Util.enableComponent(packageManager, darkIconComponent);
			Util.disableComponent(packageManager, lightIconComponent);
		} else {
			Util.disableComponent(packageManager, darkIconComponent);
			Util.enableComponent(packageManager, lightIconComponent);
		}
		this.useDarkIcon = null;
		setResult(Constants.INTENTCOMM_DONT_RECREATE);

	}


	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class Prefs1Fragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Apply default values
			// PreferenceManager.setDefaultValues(getActivity(), R.xml.advanced_preferences, false);

			// Change sharedPreferences filename
			getPreferenceManager().setSharedPreferencesName(this.getString(R.string.userprefs_filename));

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.prefs1_fragment);
		}
	}
}