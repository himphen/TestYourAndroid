package hibernate.v2.testyourandroid.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.AppListFragment;

import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_ALL;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_SYSTEM;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_USER;

public class AppListActivity extends BaseActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	private int appType = ARG_APP_TYPE_USER;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);
		switch (appType) {
			case ARG_APP_TYPE_USER:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_user);
				break;
			case ARG_APP_TYPE_SYSTEM:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_system);
				break;
			case ARG_APP_TYPE_ALL:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_all);
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_adview);

		if (getIntent() != null) {
			appType = getIntent().getIntExtra(ARG_APP_TYPE, ARG_APP_TYPE_USER);
		}

		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		switch (appType) {
			case ARG_APP_TYPE_USER:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_user);
				break;
			case ARG_APP_TYPE_SYSTEM:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_system);
				break;
			case ARG_APP_TYPE_ALL:
				initActionBar(getSupportActionBar(), R.string.title_activity_app_all);
				break;
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				adView = C.initAdView(mContext, adLayout);
			}
		}, DELAY_AD_LAYOUT);

		Fragment fragment = AppListFragment.newInstance(appType);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.removeAllViews();
			adView.destroy();
		}
		super.onDestroy();
	}

}
