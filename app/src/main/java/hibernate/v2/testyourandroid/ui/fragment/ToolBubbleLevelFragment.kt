package hibernate.v2.testyourandroid.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import kotlinx.android.synthetic.main.fragment_tool_bubble_level.*

/**
 * Created by himphen on 21/5/16.
 */
class ToolBubbleLevelFragment : BaseFragment(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var secondSensor: Sensor? = null
    private var accValues = FloatArray(3)
    private var magValues = FloatArray(3)
    private val r = FloatArray(9)
    private val values = FloatArray(3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tool_bubble_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (mSensor != null && secondSensor != null) {
            mSensorManager?.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_UI)
            mSensorManager?.registerListener(this, secondSensor,
                    SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun init() {
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            secondSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            if (mSensor == null || secondSensor == null) {
                throw Exception()
            }
        } catch (e: Exception) {
            errorNoFeatureDialog(context)
        }
    }

    override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magValues = event.values.clone()
        }
        SensorManager.getRotationMatrix(r, null, accValues, magValues)
        SensorManager.getOrientation(r, values)
        val pitchAngle = values[1]
        val rollAngle = -values[2]
        onAngleChanged(rollAngle, pitchAngle)
    }

    /**
     * @param rollAngle  float
     * @param pitchAngle float
     */
    private fun onAngleChanged(rollAngle: Float, pitchAngle: Float) {
        levelView!!.setAngle(rollAngle.toDouble(), pitchAngle.toDouble())
        horizontalTv!!.text = String.format("%s°", Math.toDegrees(rollAngle.toDouble()).toInt())
        verticalTv!!.text = String.format("%s°", Math.toDegrees(pitchAngle.toDouble()).toInt())
    }

    companion object {
        fun newInstance(): ToolBubbleLevelFragment {
            val fragment = ToolBubbleLevelFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}