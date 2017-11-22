package es.udc.psi1718.project;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import es.udc.psi1718.project.util.UserPreferencesManager;


/**
 * Activity that displays various settings for our application
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final String TAG = "SettingsActivity";
	private Context context = this;

	// Shared Preferences variables
	private SharedPreferences userPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_settings);

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
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home) {
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
			// Recreate tha activity to apply new theme
			this.recreate();
		}
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