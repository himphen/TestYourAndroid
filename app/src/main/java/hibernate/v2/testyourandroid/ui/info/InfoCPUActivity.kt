package hibernate.v2.testyourandroid.ui.info

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class InfoCPUActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoCPUFragment()
    override var titleId: Int? = R.string.title_activity_cpu
}