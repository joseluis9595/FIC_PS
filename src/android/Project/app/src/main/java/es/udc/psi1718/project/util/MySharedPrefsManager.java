package es.udc.psi1718.project.util;


import android.content.Context;

public class MySharedPrefsManager {

	private final String TAG = "MySharedPrefsManager";
	private Context context;

	public MySharedPrefsManager(Context context) {
		this.context = context;
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
