package hibernate.v2.testyourandroid.ui.hardware

import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareBiometricBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.viewBinding
import java.util.concurrent.Executor

/**
 * Created by himphen on 21/5/16.
 */
class HardwareBiometricFragment : BaseFragment(R.layout.fragment_hardware_biometric) {

    private val binding by viewBinding(FragmentHardwareBiometricBinding::bind)
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            executor = ContextCompat.getMainExecutor(context)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        binding.helpText.text = when (errorCode) {
                            BiometricConstants.ERROR_CANCELED -> context.getString(R.string.generic_error_user_canceled)
                            BiometricConstants.ERROR_HW_NOT_PRESENT -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_HW_UNAVAILABLE -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_LOCKOUT -> context.getString(R.string.fingerprint_error_lockout)
                            BiometricConstants.ERROR_LOCKOUT_PERMANENT -> context.getString(R.string.fingerprint_error_lockout)
                            BiometricConstants.ERROR_NEGATIVE_BUTTON -> context.getString(R.string.generic_error_user_canceled)
                            BiometricConstants.ERROR_NO_BIOMETRICS -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_NO_SPACE -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_TIMEOUT -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_UNABLE_TO_PROCESS -> context.getString(R.string.default_error_msg)
                            BiometricConstants.ERROR_USER_CANCELED -> context.getString(R.string.generic_error_user_canceled)
                            BiometricConstants.ERROR_VENDOR -> context.getString(R.string.default_error_msg)
                            else -> getString(R.string.default_error_msg)
                        }
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        binding.helpText.text = getString(R.string.ui_fingerprint_succeeded)
                    }

                    override fun onAuthenticationFailed() {
                        binding.helpText.text = getString(R.string.ui_fingerprint_fail)
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.fingerprint_dialog_touch_sensor))
                .setNegativeButtonText(getString(R.string.ui_cancel))
                .build()

            val biometricManager = BiometricManager.from(context)
            if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
                Utils.errorNoFeatureDialog(context)

                return
            }

            biometricPrompt.authenticate(promptInfo)

            binding.imageView1.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        biometricPrompt.cancelAuthentication()
    }

    companion object {
        fun newInstance(): HardwareBiometricFragment {
            return HardwareBiometricFragment()
        }
    }
}