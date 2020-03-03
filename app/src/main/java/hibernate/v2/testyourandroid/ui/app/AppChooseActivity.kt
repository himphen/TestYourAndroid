package hibernate.v2.testyourandroid.ui.app

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity
import hibernate.v2.testyourandroid.ui.app.AppChooseFragment

class AppChooseActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = AppChooseFragment()
    override var titleId: Int? = R.string.title_activity_app_choose
}