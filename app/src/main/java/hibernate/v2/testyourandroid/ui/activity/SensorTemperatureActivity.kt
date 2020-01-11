package hibernate.v2.testyourandroid.ui.activity

import android.hardware.Sensor
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.SensorFragment

class SensorTemperatureActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = SensorFragment.newInstance(Sensor.TYPE_AMBIENT_TEMPERATURE)
    override var titleId: Int? = R.string.title_activity_temperature
}