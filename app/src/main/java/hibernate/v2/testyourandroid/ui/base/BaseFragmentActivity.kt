package hibernate.v2.testyourandroid.ui.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import hibernate.v2.testyourandroid.helper.UtilHelper.detectLanguage
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by himphen on 21/5/16.
 */
abstract class BaseFragmentActivity : BaseActivity() {
    open var fragment: Fragment? = null
    open var titleId: Int? = null
    open var titleString: String? = null

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        detectLanguage(this)
        initActionBar(toolbar, titleString = titleString, titleId = titleId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(fragment, titleString = titleString, titleId = titleId)
    }

    fun initLauncherActivity(value: String) {
        intent?.action?.let {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value)
            when (it) {
                "SHORTCUT_LAUNCH" -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                    FirebaseAnalytics.getInstance(this).logEvent("shortcut_launch", bundle)
                }
                Intent.ACTION_MAIN -> {
                    intent?.categories?.forEach { category ->
                        if (category == Intent.CATEGORY_LAUNCHER) {

                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            FirebaseAnalytics.getInstance(this).logEvent("launch", bundle)
                            return@forEach
                        }
                    }
                }
                else -> {
                }
            }
        }
    }
}