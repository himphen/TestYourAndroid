package hibernate.v2.testyourandroid.ui.info.monitor

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class MonitorActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = MonitorFragment()
    override var titleId: Int? = R.string.app_name
}