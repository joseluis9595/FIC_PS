package es.udc.psi1718.project.storage;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import es.udc.psi1718.project.R;

/**
 * Singleton class JSONFileReader, used to read JSON files specific for our application
 */
public class MyJSONFileReader {
	private final String TAG = "MyJSONfileReader";
	private static MyJSONFileReader INSTANCE = new MyJSONFileReader();
	private String jsonString = null;

	/**
	 * Private constructor to prevent instantiation
	 */
	private MyJSONFileReader() {
	}


	/**
	 * Get instance to be able to have a singleton class
	 *
	 * @return instance of the class
	 */
	public static MyJSONFileReader getInstance() {
		return INSTANCE;
	}


	/**
	 * Loads this application main JSON file asynchronously
	 *
	 * @param context context
	 */
	public void loadJsonFileAsync(Context context) {
		new JSONFileReaderAsyncTask().execute(context);
	}


	/**
	 * AsyncTask to load a JSON file asynchronously
	 */
	private class JSONFileReaderAsyncTask extends AsyncTask<Context, Void, Void> {
		@Override
		protected Void doInBackground(Context... args) {
			if (args == null) return null;
			// InputStream inputStream = args[0];
			InputStream inputStream = args[0].getResources().openRawResource(R.raw.controllers_type);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				try {
					Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			jsonString = writer.toString();
			Log.e(TAG, jsonString);
			return null;
		}
	}

	public String getJSONString() {
		return this.jsonString;
	}

}





















