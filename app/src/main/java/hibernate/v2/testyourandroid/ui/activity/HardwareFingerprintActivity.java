package hibernate.v2.testyourandroid.ui.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.HardwareFingerprintFragment;

public class HardwareFingerprintActivity extends BaseActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);
		initActionBar(getSupportActionBar(), R.string.title_activity_fingerprint);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_adview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		initActionBar(getSupportActionBar(), R.string.title_activity_fingerprint);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					adView = C.initAdView(mContext, adLayout);
				}
			}, DELAY_AD_LAYOUT);

			Fragment fragment = new HardwareFingerprintFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment)
					.commit();
		} else {
			MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
					.title(R.string.ui_error)
					.content(getString(R.string.ui_not_support_android_version, "6.0"))
					.cancelable(false)
					.positiveText(R.string.ui_okay)
					.onPositive(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							mContext.finish();
						}
					});
			dialog.show();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test_fingerprint, menu);
		return true;
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
