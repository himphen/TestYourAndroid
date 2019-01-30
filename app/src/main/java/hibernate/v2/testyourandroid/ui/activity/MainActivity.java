package hibernate.v2.testyourandroid.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.blankj.utilcode.util.AppUtils;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.BuildConfig;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.helper.UtilHelper;
import hibernate.v2.testyourandroid.ui.fragment.MainFragment;

public class MainActivity extends BaseActivity implements RatingDialogListener {

	private SharedPreferences preferences;
	private SharedPreferences defaultPreferences;
	private BillingProcessor billingProcessor;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private String[] productIDArray = {"Buy Me A Orange Juice", "Buy Me A Coffee", "Buy Me A Big Mac"};

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
						if (C.iapProductIdList().contains(productId)) {
							defaultPreferences.edit().putBoolean(UtilHelper.PREF_IAP, true).apply();
							MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
									.title(R.string.iab_complete_title)
									.customView(R.layout.dialog_donate, true)
									.positiveText(R.string.ui_okay);
							dialog.show();
						}
					}

					@Override
					public void onPurchaseHistoryRestored() {
						for (String productId : C.iapProductIdListAll()) {
							if (billingProcessor.isPurchased(productId)) {
								defaultPreferences.edit().putBoolean(UtilHelper.PREF_IAP, true).apply();
								break;
							}
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
				openDialogLanguage();
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
			openDialogIAP();
		} else {
			Toast.makeText(mContext, R.string.ui_error, Toast.LENGTH_LONG).show();
		}
	}

	public void openDialogRate() {
		new AppRatingDialog.Builder()
				.setPositiveButtonText(R.string.rate_posbtn)
				.setNegativeButtonText(R.string.rate_navbtn)
				.setNeutralButtonText(R.string.rate_netbtn)
				.setNumberOfStars(5)
				.setDefaultRating(5)
				.setTitle(R.string.rate_title)
				.setDescription(R.string.rate_message)
				.setCommentInputEnabled(false)
				.setStarColor(R.color.gold)
				.setTitleTextColor(R.color.white)
				.setDescriptionTextColor(R.color.grey200)
				.setWindowAnimation(R.style.RatingDialogFadeAnimation)
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.create(MainActivity.this)
				.show();
	}

	private void countRate() {
		int countRate = preferences.getInt(C.PREF_COUNT_RATE, 0);
		if (countRate == 5) {
			openDialogRate();
		}
		countRate++;
		preferences.edit().putInt(C.PREF_COUNT_RATE, countRate).apply();
	}

	public void openDialogLanguage() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.title_activity_language)
				.items(R.array.language_choose)
				.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						SharedPreferences.Editor editor = defaultPreferences.edit();

						String[] languageLocaleCodeArray = mContext.getResources().getStringArray(R.array.language_locale_code);
						String[] languageLocaleCountryCodeArray = mContext.getResources().getStringArray(R.array.language_locale_country_code);

						editor.putString(C.PREF_LANGUAGE, languageLocaleCodeArray[which])
								.putString(C.PREF_LANGUAGE_COUNTRY, languageLocaleCountryCodeArray[which])
								.apply();

						startActivity(new Intent(mContext, MainActivity.class));
						mContext.finish();
						return false;
					}
				})
				.negativeText(R.string.ui_cancel);
		dialog.show();
	}

	public void openDialogIAP() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.title_activity_test_ad_remover)
				.items(productIDArray)
				.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						billingProcessor.purchase(mContext, C.iapProductIdList().get(which));
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

	@Override
	public void onNegativeButtonClicked() {
		preferences.edit().putInt(C.PREF_COUNT_RATE, 1000).apply();
	}

	@Override
	public void onNeutralButtonClicked() {
		preferences.edit().putInt(C.PREF_COUNT_RATE, 0).apply();
	}

	@Override
	public void onPositiveButtonClicked(int i, @NotNull String s) {
		preferences.edit().putInt(C.PREF_COUNT_RATE, 1000).apply();

		if (i >= 4) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			try {
				intent.setData(Uri.parse("market://details?id=hibernate.v2.testyourandroid"));
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid"));
				startActivity(intent);
			}
		} else {
			Intent intent = new Intent(Intent.ACTION_SEND);

			String text = "Android Version: " + android.os.Build.VERSION.RELEASE + "\n";
			text += "SDK Level: " + String.valueOf(android.os.Build.VERSION.SDK_INT) + "\n";
			text += "Version: " + AppUtils.getAppVersionName() + "\n";
			text += "Brand: " + Build.BRAND + "\n";
			text += "Model: " + Build.MODEL + "\n\n\n";

			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_EMAIL, "hibernatev2@gmail.com");
			intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
			intent.putExtra(Intent.EXTRA_TEXT, text);

			startActivity(intent);
		}
	}
}
