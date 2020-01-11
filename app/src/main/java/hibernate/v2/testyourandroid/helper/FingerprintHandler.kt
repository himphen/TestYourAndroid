package hibernate.v2.testyourandroid.helper

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.TextView
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.isPermissionsGranted

@TargetApi(Build.VERSION_CODES.M)
class FingerprintHandler(
        private val context: Context,
        private val helpText: TextView
) : FingerprintManager.AuthenticationCallback() {
    private var cancellationSignal: CancellationSignal? = null
    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?) {
        if (isPermissionsGranted(context, PERMISSION_NAME)) {
            cancellationSignal = CancellationSignal()
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
            helpText.setText(R.string.ui_fingerprint_start_message)
        }
    }

    fun stopAuth() {
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
        }
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        helpText.text = errString
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        helpText.text = String.format("%s\n%s", context.getString(R.string.ui_fingerprint_help), helpString)
    }

    override fun onAuthenticationFailed() {
        helpText.setText(R.string.ui_fingerprint_fail)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        helpText.setText(R.string.ui_fingerprint_succeeded)
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.USE_FINGERPRINT)
    }

}