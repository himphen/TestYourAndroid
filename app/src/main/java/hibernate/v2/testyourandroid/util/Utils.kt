package hibernate.v2.testyourandroid.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiInfo
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.himphen.logger.Logger
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.core.SharedPreferencesManager
import hibernate.v2.testyourandroid.databinding.DialogPermissionBinding
import hibernate.v2.testyourandroid.util.ext.convertDpToPx
import hibernate.v2.testyourandroid.util.ext.md5
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.util.Locale
import java.util.regex.Pattern

object Utils : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    enum class PrefTheme(val value: String) {
        THEME_AUTO("auto"),
        THEME_LIGHT("light"),
        THEME_DARK("dark")
    }

    const val DELAY_AD_LAYOUT = 100L

    fun isAdHidden(): Boolean {
        return sharedPreferencesManager.iap
    }

    fun initAdView(
        context: Context?,
        adLayout: RelativeLayout?,
        isPreserveSpace: Boolean = false,
        adUnitId: String = BuildConfig.ADMOB_BANNER_ID,
        adUnitSize: AdSize = AdSize.BANNER
    ): AdView? {
        if (context == null || adLayout == null) return null

        try {
            if (!isAdHidden()) {
                if (isPreserveSpace) {
                    adLayout.layoutParams.height = context.convertDpToPx(50)
                }
                val adView = AdView(context)
                adView.adUnitId = adUnitId
                adView.adSize = adUnitSize
                adLayout.addView(adView)
                adView.loadAd(AdRequest.Builder().build())
                return adView
            }
        } catch (e: Exception) {
            logException(e)
        }
        return null
    }

    fun updateLanguage(context: Context): Context {
        val language = sharedPreferencesManager.language
        val languageCountry = sharedPreferencesManager.languageCountry
        if (language.isNotEmpty()) {
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(Locale(language, languageCountry))
            } else {
                @Suppress("DEPRECATION")
                config.locale = Locale(language, languageCountry)
            }

            return context.createConfigurationContext(config)
        } else {
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(Locale.getDefault())
            } else {
                @Suppress("DEPRECATION")
                config.locale = Locale.getDefault()
            }

            return context.createConfigurationContext(config)
        }
    }

    fun logException(e: Exception, sendToFirebase: Boolean = true) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        } else {
            if (sendToFirebase)
                FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun scanForActivity(context: Context?): Activity? {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> scanForActivity(context.baseContext)
            else -> null
        }
    }

    fun startSettingsActivity(context: Context?, action: String?) {
        try {
            context?.startActivity(Intent(action))
        } catch (e: Exception) {
            context?.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private const val IAP_PID = "iap1984"
    private const val IAP_PID_10 = "adfree_orange"
    private const val IAP_PID_20 = "adfree_coffee"
    private const val IAP_PID_40 = "adfree_bigmac"

    @SuppressLint("SetTextI18n")
    fun openErrorPermissionDialog(context: Context?, permissions: MutableList<String>) {
        context?.let {
            val viewBinding = DialogPermissionBinding.inflate(LayoutInflater.from(context))

            val dialog = MaterialAlertDialogBuilder(context)
                .setView(viewBinding.root)
                .setCancelable(false)
                .setPositiveButton(R.string.ui_okay) { dialog, which ->
                    scanForActivity(context)?.let { activity ->
                        try {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:" + activity.packageName)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            activity.startActivity(intent)
                            activity.finish()
                        } catch (e: Exception) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_SETTINGS
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    }
                }
                .setNegativeButton(R.string.ui_cancel) { dialog, which ->
                    scanForActivity(context)?.finish()
                }

            viewBinding.messageTv.text =
                viewBinding.messageTv.text.toString() +
                "\n\n" + permissions.joinToString("\n")

            if (scanForActivity(dialog.context)?.isFinishing == false)
                dialog.show()
        }
    }

    fun errorNoFeatureDialog(context: Context?, isFinish: Boolean = true) {
        context?.let {
            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(R.string.ui_error)
                .setMessage(R.string.dialog_feature_na_message)
                .setCancelable(false)
                .setPositiveButton(R.string.ui_okay) { _, _ ->
                    if (isFinish) scanForActivity(context)?.finish()
                }

            if (scanForActivity(dialog.context)?.isFinishing == false)
                dialog.show()
        }
    }

    fun formatBitSize(size: Long, isSuffix: Boolean = true): String {
        var temp: String
        var fSize = size / 1024 / 1024.toDouble()
        val df = DecimalFormat("##.##")
        if (isSuffix) {
            var suffix = " MB"
            if (fSize >= 1024) {
                suffix = " GB"
                fSize /= 1024.0
            }
            temp = df.format(fSize)
            temp += suffix
        } else {
            temp = df.format(fSize)
        }
        return temp
    }

    fun formatSpeedSize(size: Long, isSuffix: Boolean = true): String {
        var temp: String
        var fSize = size * 8 / 1000.toDouble()
        val df = DecimalFormat("##.##")
        if (isSuffix) {
            var suffix = " Kbps"
            if (fSize >= 1000) {
                suffix = " Mbps"
                fSize /= 1000.0
            }
            temp = df.format(fSize)
            temp += suffix
        } else {
            temp = df.format(fSize)
        }
        return temp
    }

    fun notAppFound(activity: Activity?, finish: Boolean = true) {
        Toast.makeText(activity, R.string.app_not_found, Toast.LENGTH_LONG).show()
        if (finish) {
            activity?.finish()
        }
    }

    /**
     * For Android 4.4 or below
     *
     * @param packageManager PackageManager
     * @param flags          int
     * @return List<PackageInfo>
     *
     */
    fun getInstalledPackages(packageManager: PackageManager?, flags: Int): List<PackageInfo> {
        val result: MutableList<PackageInfo> = ArrayList()

        packageManager?.let {
            try {
                return packageManager.getInstalledPackages(flags)
            } catch (ignored: Exception) { // we don't care why it didn't succeed. We'll do it using an alternative way instead
            }
            // use fallback:
            var bufferedReader: BufferedReader? = null
            try {
                val process = Runtime.getRuntime().exec("pm list packages")
                bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String
                while (bufferedReader.readLine().also { line = it } != null) {
                    val packageName = line.substring(line.indexOf(':') + 1)
                    val packageInfo = packageManager.getPackageInfo(packageName, flags)
                    result.add(packageInfo)
                }
                process.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (bufferedReader != null) try {
                    bufferedReader.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return result
    }

    fun iapProductIdList(): ArrayList<String> {
        return arrayListOf(IAP_PID_10, IAP_PID_20, IAP_PID_40)
    }

    fun iapProductIdListAll(): ArrayList<String> {
        return arrayListOf(IAP_PID, IAP_PID_10, IAP_PID_20, IAP_PID_40)
    }

    fun appendHttps(urlString: String): String {
        if (URLUtil.isHttpsUrl(urlString)) {
            return urlString
        } else if (URLUtil.isHttpUrl(urlString)) {
            return urlString.replace("http://", "https://")
        }

        return "https://$urlString"
    }

    fun snackbar(
        view: View?,
        string: String? = null,
        @StringRes stringRid: Int? = null
    ): Snackbar? {
        val snackbar: Snackbar
        view?.let {
            snackbar = when {
                string != null -> Snackbar.make(view, string, Snackbar.LENGTH_LONG)
                stringRid != null -> Snackbar.make(view, stringRid, Snackbar.LENGTH_LONG)
                else -> return null
            }

            val sbView = snackbar.view
            sbView.setBackgroundResource(R.color.primary_dark)
            sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                .setTextColor(Color.WHITE)
            sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                .setTextColor(
                    ContextCompat.getColor(snackbar.context, R.color.lineColor2)
                )

            return snackbar
        }

        return null
    }

    @SuppressLint("HardwareIds")
    fun getAdMobDeviceID(context: Context): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return androidId.md5().uppercase(Locale.getDefault())
    }

    fun ipAddressIntToString(i: Int): String {
        return (i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF)
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getMacAddress(wifiInfo: WifiInfo): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return wifiInfo.macAddress
        }
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .find { networkInterface ->
                    networkInterface.name.equals(
                        "wlan0",
                        ignoreCase = true
                    )
                }
                ?.hardwareAddress
                ?.joinToString(separator = ":") { byte -> "%02X".format(byte) }
        } catch (ex: Exception) {
            Logger.e(ex, "")
            null
        }
    }

    fun updateTheme(value: String?) {
        val mode = when (value) {
            PrefTheme.THEME_LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
            PrefTheme.THEME_DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun isDarkMode(context: Context?): Boolean {
        return when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun setFullScreen(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    fun isURL(input: CharSequence?): Boolean {
        return isMatch("[a-zA-z]+://[^\\s]*", input)
    }

    fun isMatch(regex: String, input: CharSequence?): Boolean {
        return input != null && input.isNotEmpty() && Pattern.matches(regex, input)
    }

    fun isTablet(): Boolean {
        return (Resources.getSystem().configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun isLandscape(context: Context?): Boolean {
        return context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun getAppVersionName(context: Context?): String {
        val packageName = context?.packageName
        if (packageName.isNullOrEmpty()) return ""
        return try {
            context.packageManager.getPackageInfo(packageName, 0)?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    fun getPendingIntentFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            0
        }
    }
}

fun <T> retry(numOfRetries: Int, block: () -> T): T {
    var throwable: Throwable? = null
    (1..numOfRetries).forEach { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            throwable = e
            Logger.d("Failed attempt $attempt / $numOfRetries")
        }
    }
    throw throwable!!
}
