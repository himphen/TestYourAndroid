package hibernate.v2.testyourwear.ui.test

import android.hardware.Sensor
import android.os.Bundle
import hibernate.v2.testyourwear.ui.base.BaseActivity

class TestSensorAccelerometerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(TestSensorFragment.newInstance(Sensor.TYPE_ACCELEROMETER))
    }
}