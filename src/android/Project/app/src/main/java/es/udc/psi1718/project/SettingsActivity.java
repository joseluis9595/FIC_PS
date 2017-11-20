package es.udc.psi1718.project;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import es.udc.psi1718.project.util.UserPreferencesManager;


public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final String TAG = "SettingsActivity";
	private Context context = this;
	private SharedPreferences userPrefs;
	private Boolean sharedPreferencesChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(UserPreferencesManager.getInstance(this).getAppTheme());
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_settings);

		userPrefs = getSharedPreferences(getString(R.string.userprefs_filename), Context.MODE_PRIVATE);
		Log.e(TAG, "Registering listener");
		userPrefs.registerOnSharedPreferenceChangeListener(this);
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
		Log.e(TAG, "Unegistering listener");
		userPrefs.unregisterOnSharedPreferenceChangeListener(this);
		if (sharedPreferencesChanged) {
			// setResult(Constants.ACTIVITYRESULT_CHANGEDPREFS);
		}
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Log.e(TAG, "onBackPressed");
		// setResult(Constants.ACTIVITYRESULT_CHANGEDPREFS);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Overriding the home button behaviour so that the animation feels more natural
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// setResult(RESULT_OK, null);
			finish();
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		sharedPreferencesChanged = true;
		Log.e(TAG, "userPrefs changed: " + sharedPreferences.toString());
		Log.e(TAG, "key changed: " + key);
		if (key.equals(getString(R.string.userprefs_theme))) {
			//SettingsActivity.this.recreate();
			// Intent intent = new Intent(context, SettingsActivity.class);
			// startActivity(intent);
			// finish();
			this.recreate();
			Log.e(TAG, "Changed theme");
		}
	}

	// @Override
	// public void onBuildHeaders(List<Header> target) {
	// 	loadHeadersFromResource(R.xml.headers_preference, target);
	// }
	//
	// @Override
	// protected boolean isValidFragment(String fragmentName) {
	// 	return Prefs1Fragment.class.getName().equals(fragmentName);
	// }


	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class Prefs1Fragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Make sure default values are applied.  In a real app, you would
			// want this in a shared function that is used to retrieve the
			// SharedPreferences wherever they are needed.
			// PreferenceManager.setDefaultValues(getActivity(),
			// 		R.xml.advanced_preferences, false);

			getPreferenceManager().setSharedPreferencesName(this.getString(R.string.userprefs_filename));

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.prefs1_fragment);
		}
	}
}