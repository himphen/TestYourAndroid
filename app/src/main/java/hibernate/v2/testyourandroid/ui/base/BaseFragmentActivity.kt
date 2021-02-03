package hibernate.v2.testyourandroid.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import hibernate.v2.testyourandroid.R

/**
 * Created by himphen on 21/5/16.
 */
abstract class BaseFragmentActivity<T : ViewBinding> : BaseActivity<T>() {
    open var fragment: Fragment? = null
    open var titleId: Int? = null
    open var titleString: String? = null
    open var pinShortcut = false

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (pinShortcut) {
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
                val menuItem = menu.add(0, 0, 0, "Add to home screen")
                menuItem.setIcon(R.drawable.baseline_add_white_24)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                return true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }
}