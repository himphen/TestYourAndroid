package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoSystemMonitorFragment

class InfoSystemMonitorActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoSystemMonitorFragment()
    override var titleId: Int? = R.string.app_name
}