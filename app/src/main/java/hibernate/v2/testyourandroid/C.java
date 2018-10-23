package hibernate.v2.testyourandroid;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import hibernate.v2.testyourandroid.helper.UtilHelper;

public class C extends UtilHelper {

	public static final String TAG = "TAG";
	public static final String PREF = "PREF_OPTION";

	public static final String IAP_PID = "iap1984";

	public static void openErrorPermissionDialog(Context mContext) {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_caution)
				.customView(R.layout.dialog_permission, true)
				.cancelable(false)
				.negativeText(R.string.ui_cancel)
				.positiveText(R.string.dialog_permission_denied_posbtn)
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						Activity activity = scanForActivity(dialog.getContext());
						if (activity != null) {
							activity.finish();
						}
					}
				})
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						Activity activity = scanForActivity(dialog.getContext());
						if (activity != null) {
							try {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:" + activity.getPackageName()));
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								activity.startActivity(intent);
								activity.finish();
							} catch (Exception e) {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								activity.startActivity(intent);
								activity.finish();
							}
						}
					}
				});
		dialog.show();
	}

	public static void errorNoFeatureDialog(Context mContext) {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_error)
				.content(R.string.dialog_feature_na_message)
				.cancelable(false)
				.positiveText(R.string.ui_okay)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						scanForActivity(dialog.getContext()).finish();
					}
				});
		dialog.show();
	}

	public static String formatBitSize(long size, boolean isSuffix) {
		String temp;
		double fSize = size / 1024 / 1024;
		DecimalFormat df = new DecimalFormat("##.##");
		if (isSuffix) {
			String suffix = " MB";
			if (fSize >= 1024) {
				suffix = " GB";
				fSize /= 1024;
			}
			temp = df.format(fSize);
			temp += suffix;
		} else {
			temp = df.format(fSize);
		}
		return temp;
	}

	public static String formatBitSize(long size) {
		return formatBitSize(size, true);
	}

	public static String formatSpeedSize(long size, boolean isSuffix) {
		String temp;
		double fSize = size * 8 / 1000;
		DecimalFormat df = new DecimalFormat("##.##");
		if (isSuffix) {
			String suffix = " Kbps";
			if (fSize >= 1000) {
				suffix = " Mbps";
				fSize /= 1000;
			}
			temp = df.format(fSize);
			temp += suffix;
		} else {
			temp = df.format(fSize);
		}
		return temp;
	}

	public static String formatSpeedSize(long size) {
		return formatSpeedSize(size, true);
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static void notAppFound(Activity mContext) {
		Toast.makeText(mContext, R.string.app_not_found, Toast.LENGTH_LONG).show();
		mContext.finish();
	}

	private static Activity scanForActivity(Context cont) {
		if (cont == null)
			return null;
		else if (cont instanceof Activity)
			return (Activity) cont;
		else if (cont instanceof ContextWrapper)
			return scanForActivity(((ContextWrapper) cont).getBaseContext());

		return null;
	}
}
