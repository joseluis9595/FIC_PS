package es.udc.psi1718.project.util;


import android.content.Context;
import android.content.SharedPreferences;

import es.udc.psi1718.project.R;

public class UserPreferencesManager {

	private static UserPreferencesManager instance = null;
	private Context context;

	// Constants
	// public static final String USERPREFS_FILENAME = "es.udc.psi1718.easyarduino.userprefs.";
	// private final String USERPREFS_THEME = USERPREFS_FILENAME + "apptheme";
	private String sharedPrefsFileName;
	private static final int APP_THEME_DEFAULT = 0;
	private static final int APP_THEME_LIGHT = 1;
	private static final int APP_THEME_DARK = 2;
	private static final int APP_THEME_TEALORANGE = 3;

	private SharedPreferences sharedPrefs;

	/**
	 * Private Constructor to prevent instantiation
	 *
	 * @param context context
	 */
	private UserPreferencesManager(Context context) {
		this.context = context;
		this.sharedPrefsFileName = context.getString(R.string.userprefs_filename);
		this.sharedPrefs = context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE);
	}


	/**
	 * Get instance (singleton)
	 *
	 * @param context context
	 *
	 * @return unique instance of the class
	 */
	public static UserPreferencesManager getInstance(Context context) {
		if (instance == null) {
			instance = new UserPreferencesManager(context);
		}
		return instance;
	}

	/**
	 * Retrieves the selected theme from user preferences
	 *
	 * @return style resource
	 */
	public int getAppTheme() {
		// TODO recoger el array de temas directamente desde arrays.xml
		String themeString = sharedPrefs.getString(context.getString(R.string.userprefs_theme), null);
		int theme = themeString == null ? -1 : Integer.parseInt(themeString);
		// int theme = sharedPrefs.getInt(USERPREFS_THEME, -1);
		switch (theme) {
			case APP_THEME_DEFAULT:
				return R.style.AppTheme_Default;
			case APP_THEME_LIGHT:
				return R.style.AppTheme_Light;
			case APP_THEME_DARK:
				return R.style.AppTheme_Dark;
			case APP_THEME_TEALORANGE:
				return R.style.AppTheme_TealOrange;
			default:
				return R.style.AppTheme_Default;
		}
	}


	public Boolean getStartConnectionAutomatically() {
		return sharedPrefs.getBoolean(context.getString(R.string.userprefs_startconnauto), true);

	}
}
