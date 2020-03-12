package hibernate.v2.testyourandroid.ui.tool.speedtext

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolSpeedTestActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolSpeedTestFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_speed_test
}