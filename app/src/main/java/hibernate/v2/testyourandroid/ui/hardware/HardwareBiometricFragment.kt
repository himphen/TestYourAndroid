package hibernate.v2.testyourandroid.ui.hardware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import hibernate.v2.testyourandroid.App
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_hardware_biometric.*
import java.util.concurrent.Executor

/**
 * Created by himphen on 21/5/16.
 */
class HardwareBiometricFragment : BaseFragment() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hardware_biometric, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    when (errorCode) {
                        BiometricConstants.ERROR_CANCELED -> {
                            helpText.text = getString(R.string.generic_error_user_canceled)
                        }
                        BiometricConstants.ERROR_HW_NOT_PRESENT -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_HW_UNAVAILABLE -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_LOCKOUT -> {
                            helpText.text = getString(R.string.fingerprint_error_lockout)
                        }
                        BiometricConstants.ERROR_LOCKOUT_PERMANENT -> {
                            helpText.text = getString(R.string.fingerprint_error_lockout)
                        }
                        BiometricConstants.ERROR_NEGATIVE_BUTTON -> {
                            helpText.text = getString(R.string.generic_error_user_canceled)
                        }
                        BiometricConstants.ERROR_NO_BIOMETRICS -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_NO_SPACE -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_TIMEOUT -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_UNABLE_TO_PROCESS -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                        BiometricConstants.ERROR_USER_CANCELED -> {
                            helpText.text = getString(R.string.generic_error_user_canceled)
                        }
                        BiometricConstants.ERROR_VENDOR -> {
                            helpText.text = getString(R.string.default_error_msg)
                        }
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    helpText.text = getString(R.string.ui_fingerprint_succeeded)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    helpText.text = getString(R.string.ui_fingerprint_fail)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_dialog_touch_sensor))
            .setNegativeButtonText(getString(R.string.ui_cancel))
            .build()

        imageView1.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        val biometricManager = BiometricManager.from(App.context)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                helpText.text = getString(R.string.fingerprint_error_hw_not_present)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                helpText.text = getString(R.string.fingerprint_error_hw_not_available)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                helpText.text = getString(R.string.fingerprint_not_recognized)
        }
    }

    companion object {
        fun newInstance(): HardwareBiometricFragment {
            return HardwareBiometricFragment()
        }
    }
}