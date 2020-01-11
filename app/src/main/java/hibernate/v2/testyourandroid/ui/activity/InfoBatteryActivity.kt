package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoBatteryFragment

class InfoBatteryActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoBatteryFragment()
    override var titleId: Int? = R.string.title_activity_battery
}