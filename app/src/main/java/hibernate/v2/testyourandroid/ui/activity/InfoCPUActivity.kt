package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoCPUFragment

class InfoCPUActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoCPUFragment()
    override var titleId: Int? = R.string.title_activity_cpu
}