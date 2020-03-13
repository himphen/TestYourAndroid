package hibernate.v2.testyourandroid

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.appbrain.AppBrain
import com.blankj.utilcode.util.Utils
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import hibernate.v2.testyourandroid.helper.UtilHelper
import io.fabric.sdk.android.Fabric
import java.util.ArrayList

/**
 * Created by himphen on 24/5/16.
 */
class App : MultiDexApplication() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        // init logger
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        Utils.init(this)

        // init AppBrain
        initAppBrain()

        // init AdMob
        initAdMob()

        // init Crashlytics
        initCrashlytics()
    }

    private fun initAppBrain() {
        if (BuildConfig.DEBUG) {
            for (deviceId in BuildConfig.APPBRAIN_DEVICE_ID) {
                AppBrain.addTestDevice(deviceId)
            }
        }
    }

    private fun initAdMob() {
        if (BuildConfig.DEBUG) {
            val testDevices = ArrayList<String>()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            testDevices.add(UtilHelper.getAdMobDeviceID(this))

            val requestConfiguration = RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }

    private fun initCrashlytics() {
        var isGooglePlay = false
        packageManager.getInstallerPackageName(packageName)?.let { installerPackageName ->
            val allowedPackageNames = ArrayList<String>()
            allowedPackageNames.add("com.android.vending")
            allowedPackageNames.add("com.google.android.feedback")
            isGooglePlay = allowedPackageNames.contains(installerPackageName)
        }

        if (isGooglePlay || BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
            Crashlytics.setBool("isGooglePlay", isGooglePlay)
        }
    }
}