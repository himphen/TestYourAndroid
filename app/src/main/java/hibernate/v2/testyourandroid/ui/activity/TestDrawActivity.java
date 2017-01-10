package hibernate.v2.testyourandroid.ui.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.custom.TestDrawView;

public class TestDrawActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		C.detectLanguage(this);
		TestDrawView testDrawView = new TestDrawView(this);
		setContentView(testDrawView);
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
			C.openErrorDialog(this);
		} else {
			testDrawView.requestFocus();
			testDrawView.setBackgroundColor(Color.WHITE);
			Toast.makeText(this, R.string.draw_message,
					Toast.LENGTH_LONG).show();
		}
	}
}
