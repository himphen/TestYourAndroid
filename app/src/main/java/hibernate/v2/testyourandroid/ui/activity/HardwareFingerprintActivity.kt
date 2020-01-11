package hibernate.v2.testyourandroid.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.ui.fragment.HardwareFingerprintFragment
import kotlinx.android.synthetic.main.toolbar.*

class HardwareFingerprintActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = HardwareFingerprintFragment()
    override var titleId: Int? = R.string.title_activity_fingerprint

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onCreate(savedInstanceState)
        } else {
            setContentView(R.layout.activity_container_adview)

            setSupportActionBar(toolbar)
            initActionBar(supportActionBar, R.string.title_activity_fingerprint)


            MaterialDialog(this)
                    .title(R.string.ui_error)
                    .message(text = getString(R.string.ui_not_support_android_version, "6.0"))
                    .cancelable(false)
                    .positiveButton(R.string.ui_okay) { dialog -> UtilHelper.scanForActivity(dialog.context)?.finish() }
                    .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_fingerprint, menu)
        return true
    }
}