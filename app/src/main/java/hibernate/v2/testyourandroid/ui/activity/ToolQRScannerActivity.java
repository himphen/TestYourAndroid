package hibernate.v2.testyourandroid.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.ToolQRScannerFragment;

public class ToolQRScannerActivity extends BaseActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);
		initActionBar(getSupportActionBar(), R.string.title_activity_qr_scanner);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_adview);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		initActionBar(getSupportActionBar(), R.string.title_activity_qr_scanner);
		adView = C.initAdView(mContext, adLayout, true);

		Fragment fragment = new ToolQRScannerFragment();
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
					.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
							.setData(Uri.parse("LAUNCH_QR_SCANNER"));

					ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(mContext, "qr_scanner")
							.setShortLabel(getString(R.string.title_activity_qr_scanner))
							.setLongLabel(getString(R.string.title_activity_qr_scanner))
							.setIcon(IconCompat.createWithResource(mContext, R.drawable.ic_icon_qrcode))
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