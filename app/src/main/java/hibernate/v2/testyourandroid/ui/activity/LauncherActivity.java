package hibernate.v2.testyourandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent mainIntent = new Intent(LauncherActivity.this, MainActivity.class);
		LauncherActivity.this.startActivity(mainIntent);
		LauncherActivity.this.finish();
	}

}
