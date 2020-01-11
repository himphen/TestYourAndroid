package hibernate.v2.testyourandroid

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.Utils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * Created by himphen on 24/5/16.
 */
class App : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Logger.addLogAdapter(AndroidLogAdapter())
        Utils.init(this)
    }
}