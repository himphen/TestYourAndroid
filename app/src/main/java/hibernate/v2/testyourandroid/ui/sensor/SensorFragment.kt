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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.SensorHelper.getAccelerometerSensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getGravitySensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getHumiditySensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getLightSensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getMagneticSensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getPressureSensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getProximitySensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getStepCounterSensorData
import hibernate.v2.testyourandroid.helper.SensorHelper.getTemperatureCounterSensorData
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.logException
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_sensor.*
import java.util.ArrayList
import kotlin.math.exp
import kotlin.math.ln

/**
 * Created by himphen on 21/5/16.
 */
@SuppressLint("DefaultLocale")
class SensorFragment : BaseFragment() {
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
    private var list: MutableList<InfoItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sensorType = it.getInt(ARG_SENSOR_TYPE, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
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
            mSensorManager.registerListener(sensorEventListener, mSensor,
                    SensorManager.SENSOR_DELAY_UI)
            if (secondSensor != null) {
                mSensorManager.registerListener(sensorEventListener, secondSensor,
                        SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    private fun init() {
        context?.let { context ->
            list = ArrayList()
            val stringArray = resources.getStringArray(R.array.test_sensor_string_array)
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            try {
                mSensor = mSensorManager.getDefaultSensor(sensorType)
                if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                    secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                }
                if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
                }

                if (mSensor == null && secondSensor == null) {
                    UtilHelper.errorNoFeatureDialog(context)
                    return
                }
            } catch (e: Exception) {
                logException(e)
                UtilHelper.errorNoFeatureDialog(context)
                return
            }
            var isGraph2 = false
            var isGraph3 = false
            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY -> {
                    sensorEventListener = accelerometerListener
                    run {
                        isGraph3 = true
                        isGraph2 = isGraph3
                    }
                }
                Sensor.TYPE_LIGHT -> sensorEventListener = lightListener
                Sensor.TYPE_PRESSURE -> {
                    sensorEventListener = pressureListener
                    mSensor?.maximumRange?.toDouble()?.let {
                        graphView.viewport.isYAxisBoundsManual = true
                        graphView.viewport.setMinY(0.0)
                        graphView.viewport.setMaxY(it)
                    }
                }
                Sensor.TYPE_PROXIMITY -> {
                    sensorEventListener = proximityListener
                    mSensor?.maximumRange?.toDouble()?.let {
                        graphView.viewport.isYAxisBoundsManual = true
                        graphView.viewport.setMinY(0.0)
                        graphView.viewport.setMaxY(it)
                    }
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    sensorEventListener = compassListener
                    graphView.viewport.isYAxisBoundsManual = true
                    graphView.viewport.setMinY(0.0)
                    graphView.viewport.setMaxY(360.0)
                }
                Sensor.TYPE_STEP_COUNTER -> sensorEventListener = stepListener
                Sensor.TYPE_AMBIENT_TEMPERATURE -> sensorEventListener = temperatureListener
                Sensor.TYPE_RELATIVE_HUMIDITY -> sensorEventListener = humidityListener
            }
            series.color = ContextCompat.getColor(context, R.color.blue500)
            series.thickness = ConvertUtils.dp2px(4f)
            graphView.addSeries(series)
            if (isGraph2) {
                series2.color = ContextCompat.getColor(context, R.color.pink500)
                series2.thickness = 3
                graphView.addSeries(series2)
            }
            if (isGraph3) {
                series2.color = ContextCompat.getColor(context, R.color.green500)
                series2.thickness = 3
                graphView.addSeries(series3)
            }
            for (i in stringArray.indices) {
                val infoItem: InfoItem = try {
                    when (sensorType) {
                        Sensor.TYPE_ACCELEROMETER -> InfoItem(stringArray[i], getAccelerometerSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_GRAVITY -> InfoItem(stringArray[i], getGravitySensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_LIGHT -> InfoItem(stringArray[i], getLightSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_PRESSURE -> InfoItem(stringArray[i], getPressureSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_PROXIMITY -> InfoItem(stringArray[i], getProximitySensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_MAGNETIC_FIELD -> InfoItem(stringArray[i], getMagneticSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_STEP_COUNTER -> InfoItem(stringArray[i], getStepCounterSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_AMBIENT_TEMPERATURE -> InfoItem(stringArray[i], getTemperatureCounterSensorData(i, stringArray.size, reading, mSensor!!))
                        Sensor.TYPE_RELATIVE_HUMIDITY -> InfoItem(stringArray[i], getHumiditySensorData(i, stringArray.size, reading, mSensor!!))
                        else -> InfoItem(stringArray[i], getString(R.string.ui_not_support))
                    }
                } catch (e: Exception) {
                    InfoItem(stringArray[i], getString(R.string.ui_not_support))
                }
                list.add(infoItem)
            }
            adapter = InfoItemAdapter(list)
            rvlist.adapter = adapter
            graphView.gridLabelRenderer.gridColor = Color.GRAY
            graphView.gridLabelRenderer.isHighlightZeroLines = false
            graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
            graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graphView.viewport.isXAxisBoundsManual = true
            graphView.viewport.setMinX(0.0)
            graphView.viewport.setMaxX(36.0)
            graphView.viewport.isScrollable = false
            graphView.viewport.isScalable = false
        }
    }

    private val accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = ("X: " + String.format("%1.4f", event.values[0]) + " m/s²\nY: "
                    + String.format("%1.4f", event.values[1]) + " m/s²\nZ: "
                    + String.format("%1.4f", event.values[2]) + " m/s²")
            lastXValue += 1.0
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()),
                    true, 100)
            series2.appendData(DataPoint(lastXValue, event.values[1].toDouble()),
                    true, 100)
            series3.appendData(DataPoint(lastXValue, event.values[2].toDouble()),
                    true, 100)
            graphView.viewport.scrollToEnd()
            list[0].contentText = reading
            adapter?.notifyDataSetChanged()
        }
    }
    private val lightListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " lux"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.notifyDataSetChanged()
        }
    }
    private val pressureListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " hPa"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.notifyDataSetChanged()
        }
    }
    private val proximityListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = String.format("%1.2f", event.values[0]) + " cm"
            lastXValue += 1.0
            list[0].contentText = reading
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            adapter?.notifyDataSetChanged()
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
            graphView.viewport.scrollToEnd()
            adapter?.notifyDataSetChanged()
        }
    }
    private val temperatureListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            val valueC = event.values[0].toDouble()
            val valueF = valueC * 1.8 + 32
            reading = String.format("%1.2f", valueC) + " °C\n" + String.format("%1.2f", valueF) + " °F"
            series.appendData(DataPoint(lastXValue, event.values[0].toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            lastXValue += 1.0
            list[0].contentText = reading
            adapter?.notifyDataSetChanged()
        }
    }
    private val compassListener: SensorEventListener = object : SensorEventListener {
        private var accelerometerValues = FloatArray(3)
        private var magneticFieldValues = FloatArray(3)
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) magneticFieldValues = event.values
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) accelerometerValues = event.values
            val values = FloatArray(3)
            val r = FloatArray(9)
            SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues)
            SensorManager.getOrientation(r, values)
            values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
            if (values[0] < 0) {
                values[0] = values[0] + 359
            }
            lastXValue += 1.0
            series.appendData(DataPoint(lastXValue, values[0].toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            if (values[0] >= 315 || values[0] < 45) {
                reading = "N " + values[0] + "°"
            } else if (values[0] >= 45 && values[0] < 135) {
                reading = "E " + values[0] + "°"
            } else if (values[0] >= 135 && values[0] < 225) {
                reading = "S " + values[0] + "°"
            } else if (values[0] >= 225 && values[0] < 315) {
                reading = "W " + values[0] + "°"
            }
            list[0].contentText = reading
            adapter?.notifyDataSetChanged()
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
                    mLastKnownAbsoluteHumidity = calculateAbsoluteHumidity(mLastKnownTemperature, mLastKnownRelativeHumidity)
                    mLastKnownDewPoint = calculateDewPoint(mLastKnownTemperature, mLastKnownRelativeHumidity)
                }
            }
            reading = getString(R.string.ui_relative_humidity) + mLastKnownRelativeHumidity + "%"
            reading += getString(R.string.ui_absolute_humidity) + mLastKnownTemperature + "°C/" + mLastKnownAbsoluteHumidity + "%"
            reading += getString(R.string.ui_dew_point) + mLastKnownTemperature + "°C/" + mLastKnownDewPoint
        }
    }

    /* Meaning of the constants
     Dv: Absolute humidity in grams/meter3
     m: Mass constant
     Tn: Temperature constant
     Ta: Temperature constant
     Rh: Actual relative humidity in percent (%) from phone’s sensor
     Tc: Current temperature in degrees C from phone’ sensor
     A: Pressure constant in hP
     K: Temperature constant for converting to kelvin
     */
    fun calculateAbsoluteHumidity(temperature: Float, relativeHumidity: Float): Float {
        val Dv: Float
        val m = 17.62f
        val Tn = 243.12f
        val Ta = 216.7f
        val A = 6.112f
        val K = 273.15f
        Dv = (Ta * (relativeHumidity / 100) * A * exp(m * temperature / (Tn + temperature).toDouble()) / (K + temperature)).toFloat()
        return Dv
    }

    /* Meaning of the constants
    Td: Dew point temperature in degrees Celsius
    m: Mass constant
    Tn: Temperature constant
    Rh: Actual relative humidity in percent (%) from phone’s sensor
    Tc: Current temperature in degrees C from phone’ sensor
    */
    fun calculateDewPoint(temperature: Float, relativeHumidity: Float): Float {
        val Td: Float
        val m = 17.62f
        val Tn = 243.12f
        Td = (Tn * ((ln(relativeHumidity / 100.toDouble()) + m * temperature / (Tn + temperature)) / (m - (ln(relativeHumidity / 100.toDouble()) + m * temperature / (Tn + temperature))))).toFloat()
        return Td
    }

    companion object {
        const val ARG_SENSOR_TYPE = "sensorType"
        fun newInstance(sensorType: Int): SensorFragment {
            val fragment = SensorFragment()
            val args = Bundle()
            args.putInt(ARG_SENSOR_TYPE, sensorType)
            fragment.arguments = args
            return fragment
        }
    }
}