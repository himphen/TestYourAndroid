package hibernate.v2.testyourandroid.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import hibernate.v2.testyourandroid.R;
import xyz.hanks.library.SmallBang;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

	protected final String PERMISSION_NAME = Manifest.permission.USE_FINGERPRINT;

	private SmallBang mSmallBang;
	private ImageView fingerprintIv;
	private TextView helpText;
	private Context mContext;
	private CancellationSignal cancellationSignal;

	public FingerprintHandler(Context context, TextView helpText, ImageView fingerprintIv) {
		mContext = context;
		this.helpText = helpText;
		this.fingerprintIv = fingerprintIv;
		mSmallBang = SmallBang.attach2Window((Activity) mContext);
	}

	public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) throws Exception {
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {
			cancellationSignal = new CancellationSignal();
			manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
			helpText.setText(R.string.ui_fingerprint_start_message);
		}
	}

	public void stopAuth() throws Exception {
		if (cancellationSignal != null) {
			cancellationSignal.cancel();
		}
	}

	@Override
	public void onAuthenticationError(int errMsgId, CharSequence errString) {
		helpText.setText(errString);
	}

	@Override
	public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
		helpText.setText(mContext.getString(R.string.ui_fingerprint_help) + "\n" + helpString);
	}

	@Override
	public void onAuthenticationFailed() {
		helpText.setText(R.string.ui_fingerprint_fail);
	}

	@Override
	public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
		helpText.setText(R.string.ui_fingerprint_succeeded);
		mSmallBang.bang(fingerprintIv);
		fingerprintIv.performClick();
	}

}