package hibernate.v2.testyourandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hibernate.v2.testyourandroid.helper.UtilHelper;

public class C extends UtilHelper {

	public static final String PREF = "PREF_OPTION";

	public static final String IAP_PID = "iap1984";
	public static final String IAP_PID_10 = "adfree_orange";
	public static final String IAP_PID_20 = "adfree_coffee";
	public static final String IAP_PID_40 = "adfree_bigmac";

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
		errorNoFeatureDialog(mContext, true);
	}

	public static void errorNoFeatureDialog(Context mContext, Boolean isFinish) {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_error)
				.content(R.string.dialog_feature_na_message)
				.cancelable(false)
				.positiveText(R.string.ui_okay)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						if (isFinish) {
							scanForActivity(dialog.getContext()).finish();
						}
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

	/**
	 * For Android 4.4 or below
	 *
	 * @param packageManager PackageManager
	 * @param flags          int
	 * @return List<PackageInfo>
	 */
	public static List<PackageInfo> getInstalledPackages(PackageManager packageManager, int flags) {
		try {
			return packageManager.getInstalledPackages(flags);
		} catch (Exception ignored) {
			// we don't care why it didn't succeed. We'll do it using an alternative way instead
		}
		// use fallback:
		List<PackageInfo> result = new ArrayList<>();
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec("pm list packages");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				final String packageName = line.substring(line.indexOf(':') + 1);
				final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, flags);
				result.add(packageInfo);
			}
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException ignored) {

				}
		}
		return result;
	}

	public static double calculateAverage(List<Integer> marks) {
		Integer sum = 0;
		if (!marks.isEmpty()) {
			for (Integer mark : marks) {
				sum += mark;
			}
			return sum.doubleValue() / marks.size();
		}
		return sum;
	}

	public static ArrayList<String> iapProductIdList() {
		String[] array = {IAP_PID_10, IAP_PID_20, IAP_PID_40};

		return new ArrayList<>(Arrays.asList(array));
	}

	public static ArrayList<String> iapProductIdListAll() {
		String[] array = {IAP_PID, IAP_PID_10, IAP_PID_20, IAP_PID_40};

		return new ArrayList<>(Arrays.asList(array));
	}
}
