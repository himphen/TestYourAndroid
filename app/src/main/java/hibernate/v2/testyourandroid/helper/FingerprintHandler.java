package hibernate.v2.testyourandroid.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.TextView;

import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

	protected final String[] PERMISSION_NAME = {Manifest.permission.USE_FINGERPRINT};

	private TextView helpText;
	private Context mContext;
	private CancellationSignal cancellationSignal;

	public FingerprintHandler(Context context, TextView helpText) {
		mContext = context;
		this.helpText = helpText;
	}

	public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
		if (C.isPermissionsGranted(mContext, PERMISSION_NAME)) {
			cancellationSignal = new CancellationSignal();
			manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
			helpText.setText(R.string.ui_fingerprint_start_message);
		}
	}

	public void stopAuth() {
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
		helpText.setText(String.format("%s\n%s", mContext.getString(R.string.ui_fingerprint_help), helpString));
	}

	@Override
	public void onAuthenticationFailed() {
		helpText.setText(R.string.ui_fingerprint_fail);
	}

	@Override
	public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
		helpText.setText(R.string.ui_fingerprint_succeeded);
	}

}