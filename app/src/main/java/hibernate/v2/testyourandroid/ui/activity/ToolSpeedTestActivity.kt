package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.ToolSpeedTestFragment

class ToolSpeedTestActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolSpeedTestFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_speed_test
}