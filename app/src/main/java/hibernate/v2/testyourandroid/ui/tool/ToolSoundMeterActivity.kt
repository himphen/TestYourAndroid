package hibernate.v2.testyourandroid.ui.tool

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolSoundMeterActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = ToolSoundMeterFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_sound_meter
}