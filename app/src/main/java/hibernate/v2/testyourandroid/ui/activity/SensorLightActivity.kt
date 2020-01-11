package hibernate.v2.testyourandroid.ui.activity

import android.hardware.Sensor
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.SensorFragment

class SensorLightActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = SensorFragment.newInstance(Sensor.TYPE_LIGHT)
    override var titleId: Int? = R.string.title_activity_light
}