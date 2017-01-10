package hibernate.v2.testyourandroid;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class C extends Util {

	public static final String TAG = "TAG";
	public static final String PREF = "PREF_OPTION";

	public static final String IAP_PID = "iap1984";

	public static void openErrorDialog(final Activity activity) {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity)
				.title(R.string.ui_error)
				.content(R.string.dialog_feature_na_message)
				.cancelable(false)
				.positiveText(R.string.ui_okay)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						activity.finish();
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
		Toast.makeText(mContext, R.string.app_notfound, Toast.LENGTH_LONG).show();
		mContext.finish();
	}
}
