package hibernate.v2.testyourandroid

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.Utils
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.util.ArrayList

/**
 * Created by himphen on 24/5/16.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())

        Utils.init(this)

        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID)

        var isGooglePlay = false
        packageManager.getInstallerPackageName(packageName)?.let { installerPackageName ->
            val allowedPackageNames = ArrayList<String>()
            allowedPackageNames.add("com.android.vending")
            allowedPackageNames.add("com.google.android.feedback")
            isGooglePlay = allowedPackageNames.contains(installerPackageName)
        }
        Crashlytics.setBool("isGooglePlay", isGooglePlay)
    }
}