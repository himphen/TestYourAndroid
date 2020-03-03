package hibernate.v2.testyourandroid.ui.tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolFlashlightActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolFlashlightFragment()
    override var titleId: Int? = R.string.title_activity_flashlight

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncherActivity("LAUNCH_FLASHLIGHT")
    }
}