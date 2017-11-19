package es.udc.psi1718.project.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.udc.psi1718.project.R;

/**
 * Some useful functions
 */
public final class Util {


	/**
	 * Private constructor to prevent instantiation
	 */
	private Util() {
	}

	/**
	 * Checks if a given String is empty or no
	 *
	 * @param text String you want to check
	 *
	 * @return Boolean
	 */
	public static Boolean isEmptyString(String text) {
		return text == null || text.trim().length() <= 0;
	}

	/**
	 * Checks if one edit text is empty
	 *
	 * @param editText editText we want to check
	 *
	 * @return Boolean
	 */
	public static Boolean isEmptyEditText(EditText editText) {
		if (null == editText) return true;
		String string = editText.getText().toString();
		return Util.isEmptyString(string);
	}

	/**
	 * Checks if a list of editTexts is empty or not
	 *
	 * @param editTexts List of editTexts
	 *
	 * @return Boolean
	 */
	public static Boolean areEmptyEditTexts(ArrayList<EditText> editTexts) {
		if (null == editTexts) return true;
		for (EditText editText : editTexts) {
			if (isEmptyEditText(editText)) return true;
		}
		return false;
	}

	/**
	 * Hides keyboard in the activity given via parameter
	 *
	 * @param activity activity from which the function is called
	 */
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != activity.getCurrentFocus())
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getApplicationWindowToken(), 0);
	}

	/**
	 * Displays a message on UI thread
	 *
	 * @param context context
	 * @param message message to display
	 */
	public static void displayMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Displays an error on the UI thread
	 *
	 * @param context context
	 * @param error   error to display
	 */
	public static void displayError(Context context, String error) {
		String message = context.getString(R.string.err_main_text, error);
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}


	/**
	 * Read text from a text file in the /raw folder
	 *
	 * @param context  context
	 * @param resource name of the file
	 *
	 * @return String with the content of the file
	 */
	public static String readRawTextFile(Context context, int resource) {
		// Create variables
		String line;
		StringBuilder stringBuilder = new StringBuilder();

		// Initialize variables
		InputStream inputStream = context.getResources().openRawResource(resource);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


		try {
			// Read line by line the text file
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		return stringBuilder.toString();
	}
}
