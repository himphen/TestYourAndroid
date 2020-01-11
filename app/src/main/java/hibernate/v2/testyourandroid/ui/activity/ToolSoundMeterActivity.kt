package hibernate.v2.testyourandroid.ui.activity

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.ToolSoundMeterFragment

class ToolSoundMeterActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolSoundMeterFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_sound_meter
}