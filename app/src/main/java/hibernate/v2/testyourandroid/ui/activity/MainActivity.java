package hibernate.v2.testyourandroid.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.BuildConfig;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.MainFragment;

public class MainActivity extends BaseActivity {

	private SharedPreferences preferences;
	private SharedPreferences defaultPreferences;
	private BillingProcessor billingProcessor;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(mContext);

		ActionBar ab = initActionBar(getSupportActionBar(), R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setHomeButtonEnabled(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_container);
		preferences = getSharedPreferences(C.PREF, 0);
		defaultPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		ActionBar ab = initActionBar(getSupportActionBar(), R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setHomeButtonEnabled(false);

		C.forceShowMenu(mContext);

		billingProcessor = new BillingProcessor(mContext, BuildConfig.GOOGLE_IAP_KEY,
				new BillingProcessor.IBillingHandler() {
					@Override
					public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
						if (productId.equals(C.IAP_PID)) {
							defaultPreferences.edit().putBoolean(C.PREF_IAP, true).apply();
							MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
									.title(R.string.iab_complete_title)
									.customView(R.layout.dialog_donate, true)
									.positiveText(R.string.ui_okay);
							dialog.show();
						}
					}

					@Override
					public void onPurchaseHistoryRestored() {
						if (billingProcessor.isPurchased(C.IAP_PID)) {
							defaultPreferences.edit().putBoolean(C.PREF_IAP, true).apply();
						}
					}

					@Override
					public void onBillingError(int errorCode, Throwable error) {
					}

					@Override
					public void onBillingInitialized() {
					}
				});

		Fragment mainFragment = new MainFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, mainFragment)
				.commit();

		countRate();
	}

	@Override
	public void onDestroy() {
		if (billingProcessor != null)
			billingProcessor.release();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_language:
				language();
				break;
			case R.id.action_iap:
				checkPayment();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void checkPayment() {
		boolean isAvailable = BillingProcessor.isIabServiceAvailable(mContext);
		if (isAvailable) {
			billingProcessor.purchase(mContext, C.IAP_PID);
		} else {
			Toast.makeText(mContext, R.string.ui_error, Toast.LENGTH_LONG).show();
		}
	}

	public void openDialogRate() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
				.title(R.string.rate_title)
				.content(R.string.rate_message)
				.negativeText(R.string.rate_navbtn)
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						preferences.edit().putInt(C.PREF_COUNT_RATE, 1000).apply();
					}
				})
				.neutralText(R.string.rate_netbtn)
				.onNeutral(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						preferences.edit().putInt(C.PREF_COUNT_RATE, 0).apply();
					}
				})
				.positiveText(R.string.rate_posbtn)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						preferences.edit().putInt(C.PREF_COUNT_RATE, 1000).apply();

						Intent intent = new Intent(Intent.ACTION_VIEW);
						try {
							intent.setData(Uri.parse("market://details?id=hibernate.v2.testyourandroid"));
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid"));
							startActivity(intent);
						}
					}
				});
		dialog.show();
	}

	private void countRate() {
		int countRate = preferences.getInt(C.PREF_COUNT_RATE, 0);
		if (countRate == 5) {
			openDialogRate();
		}
		countRate++;
		preferences.edit().putInt(C.PREF_COUNT_RATE, countRate).apply();
	}

	private void language() {
		int a = -1;

		MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
				.title(R.string.action_language)
				.items(R.array.language_choose)
				.itemsCallbackSingleChoice(a, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						SharedPreferences.Editor editor = defaultPreferences.edit();

						String[] languageLocaleCodeArray = mContext.getResources().getStringArray(R.array.language_locale_code);
						String[] languageLocaleCountryCodeArray = mContext.getResources().getStringArray(R.array.language_locale_country_code);

						editor.putString(C.PREF_LANGUAGE, languageLocaleCodeArray[which])
								.putString(C.PREF_LANGUAGE_COUNTRY, languageLocaleCountryCodeArray[which])
								.apply();

						startActivity(new Intent(mContext, MainActivity.class));
						finish();
						return false;
					}
				})
				.negativeText(R.string.ui_cancel);
		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
