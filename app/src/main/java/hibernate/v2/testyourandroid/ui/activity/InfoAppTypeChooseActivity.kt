package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.AppChooseFragment

class InfoAppTypeChooseActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = AppChooseFragment()
    override var titleId: Int? = R.string.title_activity_app_choose
}