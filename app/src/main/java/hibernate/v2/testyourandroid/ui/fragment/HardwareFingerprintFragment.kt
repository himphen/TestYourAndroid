package hibernate.v2.testyourandroid.ui.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.FingerprintHandler
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.scanForActivity
import hibernate.v2.testyourandroid.helper.UtilHelper.startSettingsActivity
import kotlinx.android.synthetic.main.fragment_hardware_fingerprint.*
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

@TargetApi(Build.VERSION_CODES.M) /*
  Created by himphen on 21/5/16.
  http://joerichard.net/android/android-fingerprint-example/
 */
class HardwareFingerprintFragment : BaseFragment() {
    private var fingerprintManager: FingerprintManager? = null
    private var keyguardManager: KeyguardManager? = null
    private lateinit var keyStore: KeyStore


    private var fingerprintHandler: FingerprintHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hardware_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            fingerprintManager = context?.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (!isPermissionsGranted(PERMISSION_NAME)) {
                requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
            }
        } else {
            errorNoFeatureDialog(context)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startSettingsActivity(context, Settings.ACTION_SECURITY_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(PERMISSION_NAME)) {
            init()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            fingerprintHandler?.stopAuth()
        } catch (ignored: Exception) {
        }
    }

    private fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManager == null) {
                errorNoFeatureDialog(context)
            } else if (!fingerprintManager!!.isHardwareDetected) {
                errorNoFeatureDialog(context)
            } else if (!keyguardManager!!.isKeyguardSecure) {
                helpText.setText(R.string.ui_fingerprint_not_locked)
            } else if (!fingerprintManager!!.hasEnrolledFingerprints()) {
                helpText.setText(R.string.ui_fingerprint_not_register)
            } else {
                try {
                    generateKey()

                    val cryptoObject = FingerprintManager.CryptoObject(cipherInit())
                    fingerprintHandler = FingerprintHandler(context!!, helpText)
                    fingerprintHandler?.startAuth(fingerprintManager!!, cryptoObject)
                } catch (e: Exception) {
                    errorNoFeatureDialog(context)
                }
            }
        } else {
            context?.let {
                MaterialDialog(it)
                        .title(R.string.ui_error)
                        .message(text = getString(R.string.ui_not_support_android_version, "6.0"))
                        .cancelable(false)
                        .positiveButton(R.string.ui_okay) { dialog -> scanForActivity(dialog.context)?.finish() }
                        .show()
            }
        }
    }

    @Throws(Exception::class)
    private fun generateKey(): KeyGenerator? {
        val keyGenerator = try {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: Exception) {
            throw Exception("Failed to get KeyGenerator instance", e)
        }
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        } catch (e: Exception) {
            throw Exception(e)
        }

        return keyGenerator
    }

    @Throws(Exception::class)
    private fun cipherInit(): Cipher {
        val cipher = try {
            Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw Exception("Failed to get Cipher", e)
        }
        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } catch (e: KeyPermanentlyInvalidatedException) {
            throw Exception("Fail to get valid key", e)
        } catch (e: KeyStoreException) {
            throw Exception("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw Exception("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw Exception("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw Exception("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw Exception("Failed to init Cipher", e)
        }

        return cipher
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                openErrorPermissionDialog(context)
            }
        }
    }

    companion object {
        const val KEY_NAME = "example_key"
        val PERMISSION_NAME = arrayOf(Manifest.permission.USE_FINGERPRINT)
    }
}