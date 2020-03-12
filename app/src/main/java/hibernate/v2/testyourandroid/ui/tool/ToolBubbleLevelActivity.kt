package hibernate.v2.testyourandroid.ui.tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolBubbleLevelActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolBubbleLevelFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_bubble_level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncherActivity("LAUNCH_BUBBLE_LEVEL")
    }
}