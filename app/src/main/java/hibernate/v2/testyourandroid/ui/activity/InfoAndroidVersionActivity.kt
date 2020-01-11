package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoAndroidVersionFragment

class InfoAndroidVersionActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoAndroidVersionFragment()
    override var titleId: Int? = R.string.title_activity_android_version
}