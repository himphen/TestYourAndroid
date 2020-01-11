package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
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
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun initActionBar(ab: ActionBar?, title: String?, subtitle: String? = null): ActionBar? {
        ab?.let {
            ab.elevation = 100f
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeButtonEnabled(true)
            ab.title = title
            if (subtitle != null) {
                ab.subtitle = subtitle
            }
        }
        return ab
    }

    protected fun initActionBar(ab: ActionBar?, titleId: Int, subtitleId: Int = 0): ActionBar? {
        ab?.let {
            ab.elevation = 100f
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeButtonEnabled(true)
            ab.setTitle(titleId)
            if (subtitleId != 0) {
                ab.setSubtitle(subtitleId)
            }
        }
        return ab
    }

    fun setActionBarTitle(titleId: Int) {
        val ab = supportActionBar
        ab?.setTitle(titleId)
    }

    fun setActionBarTitle(title: String?) {
        val ab = supportActionBar
        if (ab != null) {
            ab.title = title
        }
    }

    companion object {
        const val DELAY_AD_LAYOUT = 100
    }

    public override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }

    fun initFragment(fragment: Fragment, title: String) {
        setContentView(R.layout.activity_container_adview)
        setSupportActionBar(toolbar)
        initActionBar(supportActionBar, title)

        Handler().postDelayed({
            adView = UtilHelper.initAdView(this, adLayout, isAdViewPreserveSpace)
        }, DELAY_AD_LAYOUT.toLong())

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }
}