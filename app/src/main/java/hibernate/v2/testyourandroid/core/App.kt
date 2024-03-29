package hibernate.v2.testyourandroid.core

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.himphen.logger.AndroidLogAdapter
import com.himphen.logger.Logger
import com.himphen.logger.PrettyFormatStrategy
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.ui.info.wifi.WifiViewModel
import hibernate.v2.testyourandroid.util.Utils.getAdMobDeviceID
import hibernate.v2.testyourandroid.util.Utils.updateTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 * Created by himphen on 24/5/16.
 */
class App : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        // init logger
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(1)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        val sharedPreferencesManager = SharedPreferencesManager(this@App)
        updateTheme(sharedPreferencesManager.theme)

        // init AdMob
        initAdMob()

        // dependency injection
        initKoin()

        // init Crashlytics
        initCrashlytics()
    }

    private fun initAdMob() {
        if (BuildConfig.DEBUG) {
            val testDevices = ArrayList<String>()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            testDevices.add(getAdMobDeviceID(this))

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }

    private fun initCrashlytics() {
        var isGooglePlay = false

        val allowedPackageNames = ArrayList<String>()
        allowedPackageNames.add("com.android.vending")
        allowedPackageNames.add("com.google.android.feedback")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(packageName).initiatingPackageName?.let { initiatingPackageName ->
                isGooglePlay = allowedPackageNames.contains(initiatingPackageName)
            }
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)?.let { installerPackageName ->
                isGooglePlay = allowedPackageNames.contains(installerPackageName)
            }
        }

        if (isGooglePlay || BuildConfig.DEBUG) {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
            Firebase.crashlytics.setCustomKey("isGooglePlay", isGooglePlay)
        }
    }

    private fun initKoin() {
        startKoin {
            // https://github.com/InsertKoinIO/koin/issues/871#issuecomment-675231528
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(appModule)
        }
    }

    // dependency injection
    private val appModule = module {
        // singleton service
        single { SharedPreferencesManager(this@App) }

        viewModel { WifiViewModel() }
    }
}
