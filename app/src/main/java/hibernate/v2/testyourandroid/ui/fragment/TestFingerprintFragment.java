package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.utils.FingerprintHandler;
import xyz.hanks.library.bang.SmallBangView;

@SuppressWarnings("FieldCanBeLocal")
@TargetApi(Build.VERSION_CODES.M)
/*
  Created by himphen on 21/5/16.
  http://joerichard.net/android/android-fingerprint-example/
 */
public class TestFingerprintFragment extends BaseFragment {

	protected final String[] PERMISSION_NAME = {Manifest.permission.USE_FINGERPRINT};

	private FingerprintManager fingerprintManager;
	private KeyguardManager keyguardManager;
	private KeyStore keyStore;
	private KeyGenerator keyGenerator;
	private static final String KEY_NAME = "example_key";
	private Cipher cipher;
	private FingerprintManager.CryptoObject cryptoObject;

	@BindView(R.id.helpText)
	TextView helpText;
	@BindView(R.id.fingerprintIvSmallBangView)
	SmallBangView fingerprintIvSmallBangView;
	private FingerprintHandler fingerprintHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_test_fingerprint, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
			fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (isPermissionsGranted(PERMISSION_NAME)) {
			init();
		} else {
			requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (fingerprintHandler != null) {
				fingerprintHandler.stopAuth();
			}
		} catch (Exception ignored) {
		}
	}

	private void init() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (fingerprintManager == null) {
				C.openErrorDialog(mContext);
			} else if (!fingerprintManager.isHardwareDetected()) {
				C.openErrorDialog(mContext);
			} else if (!keyguardManager.isKeyguardSecure()) {
				helpText.setText(R.string.ui_fingerprint_not_locked);
			} else if (!fingerprintManager.hasEnrolledFingerprints()) {
				helpText.setText(R.string.ui_fingerprint_not_register);
			} else {
				try {
					generateKey();
					cipherInit();
					cryptoObject = new FingerprintManager.CryptoObject(cipher);

					fingerprintHandler = new FingerprintHandler(mContext, helpText, fingerprintIvSmallBangView);
					fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
				} catch (Exception e) {
					C.openErrorDialog(mContext);
				}
			}
		} else {
			MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
					.title(R.string.ui_error)
					.content(R.string.dialog_fingerprint_sdk_message)
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

	private void generateKey() throws Exception {
		try {
			keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		} catch (Exception e) {
			throw new Exception("Failed to get KeyGenerator instance", e);
		}

		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);
			keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
					KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
					.build());
			keyGenerator.generateKey();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void cipherInit() throws Exception {
		try {
			cipher = Cipher.getInstance(
					KeyProperties.KEY_ALGORITHM_AES + "/"
							+ KeyProperties.BLOCK_MODE_CBC + "/"
							+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new Exception("Failed to get Cipher", e);
		}

		try {
			keyStore.load(null);
			SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (KeyPermanentlyInvalidatedException e) {
			throw new Exception("Fail to get valid key", e);
		} catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e) {
			throw new Exception("Failed to init Cipher", e);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (hasAllPermissionsGranted(grantResults)) {
				init();
			} else {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
