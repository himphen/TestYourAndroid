package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.HardwareCameraFragment

class HardwareCameraActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = HardwareCameraFragment()
    override var titleId: Int? = R.string.title_activity_camera
}