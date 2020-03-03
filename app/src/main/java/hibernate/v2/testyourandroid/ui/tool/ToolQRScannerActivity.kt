package hibernate.v2.testyourandroid.ui.tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolQRScannerActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolQRScannerFragment()
    override var titleId: Int? = R.string.title_activity_qr_scanner
    override var isAdViewPreserveSpace: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncherActivity("LAUNCH_QR_SCANNER")
    }
}