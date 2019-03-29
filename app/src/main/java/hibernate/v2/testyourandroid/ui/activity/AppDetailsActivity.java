package hibernate.v2.testyourandroid.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.ui.fragment.AppInfoFragment;

public class AppDetailsActivity extends BaseActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private AppItem appItem;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);
		initActionBar(getSupportActionBar(), appItem.getAppName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_top_tab);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			appItem = bundle.getParcelable("APP");

			initActionBar(getSupportActionBar(), appItem.getAppName());

			Fragment fragment = AppInfoFragment.newInstance(appItem);
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment)
					.commit();
		} else {
			C.notAppFound(mContext);
		}
	}

}
