package es.udc.psi1718.project.storage;


import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefsManager {
	private final String TAG = "MySharedPrefsManager";
	private static MySharedPrefsManager instance = null;

	private static final String SHAREDPREFS_FILENAME = "es.udc.psi1718.easyarduino.";
	private final String SHAREDPREFS_TIMESOPENED = SHAREDPREFS_FILENAME + "times_opened";
	private final String SHAREDPREFS_TUTORIALSEEN = SHAREDPREFS_FILENAME + "tutorial_seen";

	private SharedPreferences sharedPrefs;

	private MySharedPrefsManager(Context context) {
		this.sharedPrefs = context.getSharedPreferences(SHAREDPREFS_FILENAME, Context.MODE_PRIVATE);
	}

	/**
	 * Get instance (singleton)
	 *
	 * @param context context
	 *
	 * @return unique instance of the class
	 */
	public static MySharedPrefsManager getInstance(Context context) {
		if (instance == null) {
			instance = new MySharedPrefsManager(context);
		}
		return instance;
	}


	/**
	 * Check if it is the first time opening the app
	 *
	 * @return Boolean
	 */
	public Boolean isFirstTimeOpening() {
		if (sharedPrefs.getInt(SHAREDPREFS_TIMESOPENED, -1) == -1) {
			sharedPrefs.edit().putInt(SHAREDPREFS_TIMESOPENED, 1).apply();
			return true;
		}
		return false;
	}


	/**
	 * Check if the tutorial was seen by the user
	 *
	 * @return Boolean
	 */
	public Boolean shouldShowTutorial() {
		return sharedPrefs.getInt(SHAREDPREFS_TUTORIALSEEN, -1) == -1;
	}

	/**
	 * Marks tutorial as seen by the user
	 */
	public void setTutorialSeen() {
		sharedPrefs.edit().putInt(SHAREDPREFS_TUTORIALSEEN, 1).apply();
	}

	/* TODO IT2-3
	 *
	 * Crear sharedPrefs para guardar el estado de los interruptores
	 * Cuando se cierre la conexión con Arduino, borrar todos los valores de
	 * estado, de esta manera, aunque Arduino no nos permita conocer el estado
	 * de los sensores conectados a los pines, lo haremos de forma interna
	 * en nuestra aplicación
	 *
	 *
	 * Las funciones que usaremos serán las siguientes:
	 *
	 *      void  saveState              (int controllerId, int value)
	 *      int   getState               (int controllerId)
	 *      void  deleteAllStatesSaved   ()
	 */
}
