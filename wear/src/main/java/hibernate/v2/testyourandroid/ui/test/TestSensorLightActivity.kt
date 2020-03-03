package hibernate.v2.testyourandroid.ui.test

import android.hardware.Sensor
import android.os.Bundle
import hibernate.v2.testyourandroid.ui.base.BaseActivity

class TestSensorLightActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(TestSensorFragment.newInstance(Sensor.TYPE_LIGHT))
    }
}