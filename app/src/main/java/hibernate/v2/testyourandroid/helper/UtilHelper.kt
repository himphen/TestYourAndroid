package hibernate.v2.testyourandroid.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.ViewConfiguration
import android.webkit.URLUtil
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blankj.utilcode.util.SizeUtils
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.Environment
import hibernate.v2.testyourandroid.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.Locale

object UtilHelper {
    const val PREF_IAP = "iap"
    const val PREF_LANGUAGE = "PREF_LANGUAGE"
    const val PREF_LANGUAGE_COUNTRY = "PREF_LANGUAGE_COUNTRY"
    const val PREF_COUNT_RATE = "PREF_COUNT_RATE"

    fun initAdView(context: Context?, adLayout: RelativeLayout): AdView? {
        return initAdView(context, adLayout, false)
    }

    fun initAdView(context: Context?, adLayout: RelativeLayout, isPreserveSpace: Boolean): AdView? {
        if (isPreserveSpace) {
            adLayout.layoutParams.height = SizeUtils.dp2px(50f)
        }
        val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var adView: AdView? = null
        try {
            if (!defaultPreferences.getBoolean(PREF_IAP, false)) {
                adView = AdView(context)
                adView.adUnitId = BuildConfig.ADMOB_BANNER_ID
                adView.adSize = AdSize.BANNER
                adLayout.addView(adView)
                val adRequest = AdRequest.Builder()
                adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                for (id in BuildConfig.ADMOB_DEVICE_ID) {
                    adRequest.addTestDevice(id)
                }
                adView.loadAd(adRequest.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return adView
    }

    fun forceShowMenu(context: Context?) {
        try {
            val config = ViewConfiguration.get(context)
            val menuKeyField = ViewConfiguration::class.java
                    .getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.isAccessible = true
            menuKeyField.setBoolean(config, false)
        } catch (ignored: Exception) {
        }
    }

    @Suppress("DEPRECATION")
    fun detectLanguage(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var language = preferences.getString(PREF_LANGUAGE, "") ?: ""
        var languageCountry = preferences.getString(PREF_LANGUAGE_COUNTRY, "") ?: ""
        if (language == "") {
            val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0]
            } else {
                Resources.getSystem().configuration.locale
            }
            language = locale.language
            languageCountry = locale.country
        }
        val res = context.resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conf.setLocale(Locale(language, languageCountry))
        } else {
            conf.locale = Locale(language, languageCountry)
        }
        val dm = res.displayMetrics
        res.updateConfiguration(conf, dm)
    }

    fun logException(e: Exception) {
        if (Environment.CONFIG.isDebug) {
            e.printStackTrace()
        } else {
            Crashlytics.logException(e)
        }
    }

    fun isPermissionsGranted(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
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

    const val PREF = "PREF_OPTION"
    private const val IAP_PID = "iap1984"
    private const val IAP_PID_10 = "adfree_orange"
    private const val IAP_PID_20 = "adfree_coffee"
    private const val IAP_PID_40 = "adfree_bigmac"

    fun openErrorPermissionDialog(context: Context?) {
        context?.let {
            MaterialDialog(it)
                    .title(R.string.ui_caution)
                    .customView(R.layout.dialog_permission)
                    .cancelable(false)
                    .positiveButton(R.string.ui_okay) { dialog ->
                        scanForActivity(dialog.context)?.let { activity ->
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
                    .negativeButton(R.string.ui_cancel) { dialog ->
                        scanForActivity(dialog.context)?.finish()
                    }
                    .show()
        }
    }

    fun errorNoFeatureDialog(context: Context?, isFinish: Boolean = true) {
        context?.let {
            MaterialDialog(it)
                    .title(R.string.ui_error)
                    .message(R.string.dialog_feature_na_message)
                    .cancelable(false)
                    .positiveButton(R.string.ui_okay) { dialog ->
                        if (isFinish) scanForActivity(dialog.context)?.finish()
                    }
                    .show()
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

    fun round(value: Double, places: Int): Double {
        var bd: BigDecimal?
        try {
            bd = BigDecimal(value)
        } catch (ex: Exception) {
            return 0.0
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun notAppFound(activity: Activity?) {
        notAppFound(activity, true)
    }

    fun notAppFound(activity: Activity?, finish: Boolean) {
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

    fun calculateAverage(marks: List<Int>): Double {
        var sum = 0
        if (marks.isNotEmpty()) {
            for (mark in marks) {
                sum += mark
            }
            return sum.toDouble() / marks.size
        }
        return sum.toDouble()
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
}