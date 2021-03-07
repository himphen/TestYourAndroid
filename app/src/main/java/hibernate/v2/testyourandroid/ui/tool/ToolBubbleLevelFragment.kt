package hibernate.v2.testyourandroid.ui.tool

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.databinding.FragmentToolBubbleLevelBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog

/**
 * Created by himphen on 21/5/16.
 */
class ToolBubbleLevelFragment : BaseFragment<FragmentToolBubbleLevelBinding>(),
    SensorEventListener {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentToolBubbleLevelBinding =
        FragmentToolBubbleLevelBinding.inflate(inflater, container, false)

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var secondSensor: Sensor? = null
    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private val r = FloatArray(9)
    private val i = FloatArray(9)
    private val orientations = FloatArray(3)

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
            mSensorManager?.registerListener(
                this, mSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            mSensorManager?.registerListener(
                this, secondSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
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
        val alpha = 0.97f
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic[0] =
                alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
            mGeomagnetic[1] =
                alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
            mGeomagnetic[2] =
                alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravity[0] =
                alpha * mGravity[0] + (1 - alpha) * event.values[0]
            mGravity[1] =
                alpha * mGravity[1] + (1 - alpha) * event.values[1]
            mGravity[2] =
                alpha * mGravity[2] + (1 - alpha) * event.values[2]
        }
        val success = SensorManager.getRotationMatrix(
            r,
            i,
            mGravity,
            mGeomagnetic
        )

        if (success) {
            SensorManager.getOrientation(r, orientations)
            val pitchAngle = orientations[1]
            val rollAngle = -orientations[2]
            onAngleChanged(rollAngle, pitchAngle)
        }
    }

    /**
     * @param rollAngle  float
     * @param pitchAngle float
     */
    private fun onAngleChanged(rollAngle: Float, pitchAngle: Float) {
        viewBinding?.levelView?.setAngle(rollAngle.toDouble(), pitchAngle.toDouble())
        viewBinding?.horizontalTv?.text =
            String.format("%s°", Math.toDegrees(rollAngle.toDouble()).toInt())
        viewBinding?.verticalTv?.text =
            String.format("%s°", Math.toDegrees(pitchAngle.toDouble()).toInt())
    }

    companion object {
        fun newInstance(): ToolBubbleLevelFragment {
            return ToolBubbleLevelFragment()
        }
    }
}