package hibernate.v2.testyourandroid.ui.activity

import android.hardware.Sensor
import android.os.Bundle
import hibernate.v2.testyourandroid.ui.fragment.TestSensorFragment

class TestSensorAccelerometerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(TestSensorFragment.newInstance(Sensor.TYPE_ACCELEROMETER))
    }
}