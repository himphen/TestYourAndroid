package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoGSMFragment

class InfoGSMActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoGSMFragment()
    override var titleId: Int? = R.string.title_activity_gsm
}