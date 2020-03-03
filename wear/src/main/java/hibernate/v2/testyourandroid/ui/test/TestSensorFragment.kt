package hibernate.v2.testyourandroid.ui.test

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.SensorHelper
import hibernate.v2.testyourandroid.model.InfoHeader
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class TestSensorFragment : BaseFragment() {
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null
    private var sensorType = 0
    private var reading = ""
    private lateinit var adapter: InfoItemAdapter
    private var list: MutableList<InfoItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorType = arguments?.getInt(ARG_SENSOR_TYPE, 0) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    override fun onPause() {
        super.onPause()
        if (sensor != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sensor != null && sensorEventListener != null) {
            sensorManager.registerListener(sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun init() {
        list = ArrayList()
        val stringArray = resources.getStringArray(R.array.test_sensor_string_array)
        sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor == null) {
                Toast.makeText(context, R.string.dialog_feature_na_message, Toast.LENGTH_LONG).show()
                activity?.finish()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.dialog_feature_na_message, Toast.LENGTH_LONG).show()
            activity?.finish()
            return
        }
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY -> sensorEventListener = accelerometerListener
            Sensor.TYPE_LIGHT -> sensorEventListener = lightListener
            Sensor.TYPE_PRESSURE -> sensorEventListener = pressureListener
            Sensor.TYPE_PROXIMITY -> sensorEventListener = proximityListener
            else -> {
            }
        }
        var infoItem: InfoItem
        for (i in stringArray.indices) {
            infoItem = try {
                when (sensorType) {
                    Sensor.TYPE_ACCELEROMETER -> InfoItem(stringArray[i], SensorHelper.getAccelerometerSensorData(i, stringArray.size, reading, sensor!!))
                    Sensor.TYPE_GRAVITY -> InfoItem(stringArray[i], SensorHelper.getGravitySensorData(i, stringArray.size, reading, sensor!!))
                    Sensor.TYPE_LIGHT -> InfoItem(stringArray[i], SensorHelper.getLightSensorData(i, stringArray.size, reading, sensor!!))
                    Sensor.TYPE_PRESSURE -> InfoItem(stringArray[i], SensorHelper.getPressureSensorData(i, stringArray.size, reading, sensor!!))
                    Sensor.TYPE_PROXIMITY -> InfoItem(stringArray[i], SensorHelper.getProximitySensorData(i, stringArray.size, reading, sensor!!))
                    else -> InfoItem(stringArray[i], getString(R.string.ui_not_support))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                InfoItem(stringArray[i], getString(R.string.ui_not_support))
            }
            list.add(infoItem)
        }
        adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        rvlist.adapter = adapter
    }

    private val accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        @SuppressLint("DefaultLocale")
        override fun onSensorChanged(event: SensorEvent) {
            reading = ("X: " + String.format("%1.4f", event.values[0]) + " m/s²\nY: "
                    + String.format("%1.4f", event.values[1]) + " m/s²\nZ: "
                    + String.format("%1.4f", event.values[2]) + " m/s²")
            list[0].contentText = reading
            adapter.notifyDataSetChanged()
        }
    }
    private val lightListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " lux"
            list[0].contentText = reading
            adapter.notifyDataSetChanged()
        }
    }
    private val pressureListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            reading = event.values[0].toString() + " hPa"
            list[0].contentText = reading
            adapter.notifyDataSetChanged()
        }
    }
    private val proximityListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        @SuppressLint("DefaultLocale")
        override fun onSensorChanged(event: SensorEvent) {
            reading = String.format("%1.2f", event.values[0]) + " cm"
            list[0].contentText = reading
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val ARG_SENSOR_TYPE = "sensorType"
        fun newInstance(sensorType: Int): TestSensorFragment {
            val fragment = TestSensorFragment()
            val args = Bundle()
            args.putInt(ARG_SENSOR_TYPE, sensorType)
            fragment.arguments = args
            return fragment
        }
    }
}