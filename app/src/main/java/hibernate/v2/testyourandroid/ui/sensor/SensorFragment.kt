package hibernate.v2.testyourandroid.ui.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentSensorBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.SensorUtils.getAccelerometerSensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getGravitySensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getHumiditySensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getLightSensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getMagneticSensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getPressureSensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getProximitySensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getStepCounterSensorData
import hibernate.v2.testyourandroid.util.SensorUtils.getTemperatureCounterSensorData
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.logException
import hibernate.v2.testyourandroid.util.ext.convertDpToPx
import kotlin.math.exp
import kotlin.math.ln

/**
 * Created by himphen on 21/5/16.
 */
@SuppressLint("DefaultLocale")
class SensorFragment : BaseFragment<FragmentSensorBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSensorBinding =
        FragmentSensorBinding.inflate(inflater, container, false)

    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var secondSensor: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null
    private var sensorType = 0
    private var reading = ""
    private var initReading = 0f
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var series2 = LineGraphSeries(arrayOf<DataPoint>())
    private var series3 = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private var adapter: InfoItemAdapter? = null
    private lateinit var list: MutableList<InfoItem>
    private lateinit var formatter: CompassDirectionsFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorType = arguments?.getInt(ARG_SENSOR_TYPE, 0) ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onPause() {
        super.onPause()
        if (mSensor != null && sensorEventListener != null) {
            mSensorManager.unregisterListener(sensorEventListener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mSensor != null && sensorEventListener != null) {
            mSensorManager.registerListener(
                sensorEventListener, mSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            if (secondSensor != null) {
                mSensorManager.registerListener(
                    sensorEventListener, secondSensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
        }
    }

    private fun init() {
        viewBinding?.let { viewBinding ->
            context?.let { context ->
                list = ArrayList()
                mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                try {
                    mSensor = mSensorManager.getDefaultSensor(sensorType)
                    if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                        secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    }
                    if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                        secondSensor =
                            mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
                    }

                    if (mSensor == null && secondSensor == null) {
                        Utils.errorNoFeatureDialog(context)
                        return
                    }
                } catch (e: Exception) {
                    logException(e)
                    Utils.errorNoFeatureDialog(context)
                    return
                }

                initSensor()

                if (sensorType != Sensor.TYPE_MAGNETIC_FIELD) {
                    initGraphView(context, viewBinding)
                    viewBinding.graphView.visibility = View.VISIBLE
                } else {
                    formatter = CompassDirectionsFormatter(context)
                    viewBinding.compassViewRl.visibility = View.VISIBLE
                }

                initRecyclerview(viewBinding)
            }
        }
    }

    private fun initSensor() {
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY -> sensorEventListener = accelerometerListener
            Sensor.TYPE_LIGHT -> sensorEventListener = lightListener
            Sensor.TYPE_PRESSURE -> sensorEventListener = pressureListener
            Sensor.TYPE_PROXIMITY -> sensorEventListener = proximityListener
            Sensor.TYPE_MAGNETIC_FIELD -> sensorEventListener = compassListener
            Sensor.TYPE_STEP_COUNTER -> sensorEventListener = stepListener
            Sensor.TYPE_AMBIENT_TEMPERATURE -> sensorEventListener = temperatureListener
            Sensor.TYPE_RELATIVE_HUMIDITY -> sensorEventListener = humidityListener
        }
    }

    private fun initGraphView(context: Context, viewBinding: FragmentSensorBinding) {
        var isGraph2 = false
        var isGraph3 = false
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> {
                mSensor?.maximumRange?.toDouble()?.let {
                    isGraph3 = true
                    isGraph2 = true
                    viewBinding.graphView.viewport.isYAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinY(-it)
                    viewBinding.graphView.viewport.setMaxY(it)
                }
            }
            Sensor.TYPE_GRAVITY -> {
                isGraph3 = true
                isGraph2 = true
                mSensor?.maximumRange?.toDouble()?.let {
                    viewBinding.graphView.viewport.isYAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinY(-it)
                    viewBinding.graphView.viewport.setMaxY(it)
                }
            }
            Sensor.TYPE_PRESSURE -> {
                mSensor?.maximumRange?.toDouble()?.let {
                    viewBinding.graphView.viewport.isYAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinY(0.0)
                    viewBinding.graphView.viewport.setMaxY(it)
                }
            }
            Sensor.TYPE_PROXIMITY -> {
                mSensor?.maximumRange?.toDouble()?.let {
                    viewBinding.graphView.viewport.isYAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinY(0.0)
                    viewBinding.graphView.viewport.setMaxY(it)
                }
            }
        }
        series.color = ContextCompat.getColor(context, R.color.lineColor3)
        series.thickness = context.convertDpToPx(4)
        viewBinding.graphView.addSeries(series)
        if (isGraph2) {
            series2.color = ContextCompat.getColor(context, R.color.lineColor1)
            series2.thickness = context.convertDpToPx(4)
            viewBinding.graphView.addSeries(series2)
        }
        if (isGraph3) {
            series3.color = ContextCompat.getColor(context, R.color.lineColor4)
            series3.thickness = context.convertDpToPx(4)
            viewBinding.graphView.addSeries(series3)
        }
        viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
        viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
        viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
        viewBinding.graphView.gridLabelRenderer.padding = context.convertDpToPx(10)
        viewBinding.graphView.gridLabelRenderer.gridStyle =
            GridLabelRenderer.GridStyle.HORIZONTAL
        viewBinding.graphView.viewport.isXAxisBoundsManual = true
        viewBinding.graphView.viewport.setMinX(0.0)
        viewBinding.graphView.viewport.setMaxX(36.0)
        viewBinding.graphView.viewport.isScrollable = false
        viewBinding.graphView.viewport.isScalable = false
    }

    private fun initRecyclerview(viewBinding: FragmentSensorBinding) {
        val stringArray = resources.getStringArray(R.array.test_sensor_string_array)
        for (i in stringArray.indices) {
            val infoItem: InfoItem = try {
                when (sensorType) {
                    Sensor.TYPE_ACCELEROMETER -> InfoItem(
                        stringArray[i],
                        getAccelerometerSensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_GRAVITY -> InfoItem(
                        stringArray[i],
                        getGravitySensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_LIGHT -> InfoItem(
                        stringArray[i],
                        getLightSensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_PRESSURE -> InfoItem(
                        stringArray[i],
                        getPressureSensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_PROXIMITY -> InfoItem(
                        stringArray[i],
                        getProximitySensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_MAGNETIC_FIELD -> InfoItem(
                        stringArray[i],
                        getMagneticSensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_STEP_COUNTER -> InfoItem(
                        stringArray[i],
                        getStepCounterSensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> InfoItem(
                        stringArray[i],
                        getTemperatureCounterSensorData(
                            i,
                            stringArray.size,
                            reading,
                            mSensor!!
                        )
                    )
                    Sensor.TYPE_RELATIVE_HUMIDITY -> InfoItem(
                        stringArray[i],
                        getHumiditySensorData(i, stringArray.size, reading, mSensor!!)
                    )
                    else -> InfoItem(stringArray[i], getString(R.string.ui_not_support))
                }
            } catch (e: Exception) {
                InfoItem(stringArray[i], getString(R.string.ui_not_support))
            }
            list.add(infoItem)
        }
        adapter = InfoItemAdapter().apply {
            submitList(list)
        }
        viewBinding.rvlist.adapter = adapter
    }

    private val accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = (
                "X: " + String.format("%1.4f", event.values[0]) + " m/s²\nY: " +
                    String.format("%1.4f", event.values[1]) + " m/s²\nZ: " +
                    String.format("%1.4f", event.values[2]) + " m/s²"
                )
            lastXValue += 1.0
            series.appendData(
                DataPoint(lastXValue, event.values[0].toDouble()),
                true, 100
            )
            series2.appendData(
                DataPoint(lastXValue, event.values[1].toDouble()),
                true, 100
            )
            series3.appendData(
                DataPoint(lastXValue, event.values[2].toDouble()),
                true, 100
            )
            viewBinding?.graphView?.viewport?.scrollToEnd()
            list[0].contentText = reading
            adapter?.submitList(list)
        }
    }
    private val lightListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " lux"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.submitList(list)
        }
    }
    private val pressureListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " hPa"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.submitList(list)
        }
    }
    private val proximityListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = String.format("%1.2f", event.values[0]) + " cm"
            lastXValue += 1.0
            list[0].contentText = reading
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            adapter?.submitList(list)
        }
    }
    private val stepListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (initReading == 0f) {
                initReading = event.values[0]
            }
            val value = (event.values[0] - initReading).toInt()
            reading = "$value Steps"
            lastXValue += 1.0
            list[0].contentText = reading
            series.appendData(DataPoint(lastXValue, value.toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            adapter?.submitList(list)
        }
    }
    private val temperatureListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            val valueC = event.values[0].toDouble()
            val valueF = valueC * 1.8 + 32
            reading =
                String.format("%1.2f", valueC) + " °C\n" + String.format("%1.2f", valueF) + " °F"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.submitList(list)
        }
    }
    private val compassListener: SensorEventListener = object : SensorEventListener {
        private var mGravity = FloatArray(3)
        private var mGeomagnetic = FloatArray(3)
        private var azimuth = 0f
        private var currentAzimuth = 0f

        val orientations = FloatArray(3)
        private val r = FloatArray(9)
        private val i = FloatArray(9)

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
                azimuth = Math.toDegrees(orientations[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                viewBinding?.graphView?.viewport?.scrollToEnd()
                list[0].contentText = formatter.format(azimuth)
                adapter?.submitList(list)

                val an: Animation = RotateAnimation(
                    -currentAzimuth, -azimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                currentAzimuth = azimuth

                an.duration = 500
                an.repeatCount = 0
                an.fillAfter = true

                viewBinding?.imageViewCompass?.startAnimation(an)
            }
        }
    }
    private var mLastKnownRelativeHumidity = 0f
    private var mLastKnownTemperature = 0f
    private var mLastKnownAbsoluteHumidity = 0f
    private var mLastKnownDewPoint = 0f
    private val humidityListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                mLastKnownRelativeHumidity = event.values[0]
            } else if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                if (mLastKnownRelativeHumidity != 0f) {
                    mLastKnownTemperature = event.values[0]
                    mLastKnownAbsoluteHumidity =
                        calculateAbsoluteHumidity(mLastKnownTemperature, mLastKnownRelativeHumidity)
                    mLastKnownDewPoint =
                        calculateDewPoint(mLastKnownTemperature, mLastKnownRelativeHumidity)
                }
            }
            reading = getString(R.string.ui_relative_humidity) + mLastKnownRelativeHumidity + "%"
            reading += getString(R.string.ui_absolute_humidity) + mLastKnownTemperature + "°C/" + mLastKnownAbsoluteHumidity + "%"
            reading += getString(R.string.ui_dew_point) + mLastKnownTemperature + "°C/" + mLastKnownDewPoint
        }
    }

    fun calculateAbsoluteHumidity(temperature: Float, relativeHumidity: Float): Float {
        return (Ta * (relativeHumidity / 100) * A * exp(MASS * temperature / (TN + temperature)) / (K + temperature))
    }

    fun calculateDewPoint(temperature: Float, relativeHumidity: Float): Float {
        return (
            TN * (
                (ln(relativeHumidity / 100) + MASS * temperature / (TN + temperature)) /
                    (MASS - (ln(relativeHumidity / 100) + MASS * temperature / (TN + temperature)))
                )
            )
    }

    companion object {
        const val ARG_SENSOR_TYPE = "sensorType"

        /*
         Meaning of the constants
         M: Mass constant
         TN: Temperature constant
         TA: Temperature constant
         A: Pressure constant in hP
         K: Temperature constant for converting to kelvin
         */
        const val TN = 243.12f
        const val MASS = 17.62f
        const val K = 273.15f
        const val A = 6.112f
        const val Ta = 216.7f

        fun newInstance(sensorType: Int): SensorFragment {
            val fragment = SensorFragment()
            val args = Bundle()
            args.putInt(ARG_SENSOR_TYPE, sensorType)
            fragment.arguments = args
            return fragment
        }
    }
}
