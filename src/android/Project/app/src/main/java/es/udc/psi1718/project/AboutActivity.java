package es.udc.psi1718.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView tvVersionName = (TextView) findViewById(R.id.tv_versionname);
		tvVersionName.setText(BuildConfig.VERSION_NAME);
	}
}
