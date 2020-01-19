package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.appbrain.AppBrain
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.detectLanguage
import kotlinx.android.synthetic.main.activity_container_adview.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by himphen on 21/5/16.
 */
abstract class BaseActivity : AppCompatActivity() {
    open var isAdViewPreserveSpace = false

    private var adView: AdView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detectLanguage(this)
        AppBrain.init(this)
        for (deviceId in BuildConfig.APPBRAIN_DEVICE_ID) {
            AppBrain.addTestDevice(deviceId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun initActionBar(
            toolbar: Toolbar,
            titleString: String? = null, subtitleString: String? = null,
            @StringRes titleId: Int? = null, @StringRes subtitleId: Int? = null
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.let { ab ->
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeButtonEnabled(true)
            titleString?.let {
                ab.title = titleString
            }
            titleId?.let {
                ab.setTitle(titleId)
            }
            subtitleString?.let {
                ab.subtitle = subtitleString
            }
            subtitleId?.let {
                ab.setSubtitle(subtitleId)
            }
        }
    }

    public override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }

    fun initFragment(fragment: Fragment?, titleString: String?, titleId: Int?) {
        fragment?.let {
            setContentView(R.layout.activity_container_adview)
            initActionBar(toolbar, titleString = titleString, titleId = titleId)

            Handler().postDelayed({
                adView = UtilHelper.initAdView(this, adLayout, isAdViewPreserveSpace)
            }, UtilHelper.DELAY_AD_LAYOUT)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
        }
    }
}