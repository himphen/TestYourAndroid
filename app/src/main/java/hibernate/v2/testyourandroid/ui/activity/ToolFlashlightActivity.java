package hibernate.v2.testyourandroid.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.ToolFlashlightFragment;

public class ToolFlashlightActivity extends BaseActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_adview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		initActionBar(getSupportActionBar(), R.string.title_activity_flashlight);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				adView = C.initAdView(mContext, adLayout);
			}
		}, DELAY_AD_LAYOUT);

		Fragment fragment = new ToolFlashlightFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ShortcutManagerCompat.isRequestPinShortcutSupported(mContext)) {
			MenuItem menuItem = menu.add(0, 0, 0, "Add to home screen");
			menuItem.setIcon(R.drawable.baseline_add_white_24)
					.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case 0:
				if (ShortcutManagerCompat.isRequestPinShortcutSupported(mContext)) {
					Intent shortcutIntent = new Intent();
					shortcutIntent.setAction("SHORTCUT_LAUNCH")
							.setData(Uri.parse("LAUNCH_FLASHLIGHT"));

					ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(mContext, "flashlight")
							.setShortLabel(getString(R.string.title_activity_flashlight))
							.setLongLabel(getString(R.string.title_activity_flashlight))
							.setIcon(IconCompat.createWithResource(mContext, R.drawable.ic_icon_flashlight))
							.setIntent(shortcutIntent)
							.build();

					Intent pinnedShortcutCallbackIntent =
							ShortcutManagerCompat.createShortcutResultIntent(mContext, shortcut);

					PendingIntent successCallback = PendingIntent.getBroadcast(mContext, 0,
							pinnedShortcutCallbackIntent, 0);

					ShortcutManagerCompat.requestPinShortcut(
							mContext, shortcut,
							successCallback.getIntentSender());
				}
		}

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
