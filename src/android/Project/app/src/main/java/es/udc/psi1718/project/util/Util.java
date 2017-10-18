package es.udc.psi1718.project.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	 * @return
	 */
	public static Boolean isEmptyString(String text) {
		return text.trim().length() <= 0;
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
