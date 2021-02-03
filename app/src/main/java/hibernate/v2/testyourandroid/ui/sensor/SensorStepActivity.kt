package hibernate.v2.testyourandroid.ui.sensor

import android.hardware.Sensor
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class SensorStepActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = SensorFragment.newInstance(Sensor.TYPE_STEP_COUNTER)
    override var titleId: Int? = R.string.title_activity_step
}