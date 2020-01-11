package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.ToolBubbleLevelFragment

class ToolBubbleLevelActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolBubbleLevelFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_bubble_level
}