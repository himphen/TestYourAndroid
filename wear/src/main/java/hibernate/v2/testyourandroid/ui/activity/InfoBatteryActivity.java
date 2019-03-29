package hibernate.v2.testyourandroid.ui.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.InfoBatteryFragment;

public class InfoBatteryActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_no_drawer);

		Fragment fragment = new InfoBatteryFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

}
