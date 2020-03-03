package hibernate.v2.testyourandroid.ui.info.monitor

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class MonitorActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = MonitorFragment()
    override var titleId: Int? = R.string.app_name
}