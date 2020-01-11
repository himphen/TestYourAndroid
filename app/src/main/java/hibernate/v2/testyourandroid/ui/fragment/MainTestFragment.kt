package hibernate.v2.testyourandroid.ui.fragment

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ScreenUtils
import com.divyanshu.draw.activity.DrawingActivity
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.activity.HardwareCameraActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareFingerprintActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareLocationActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareMicrophoneActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareNFCActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareScreenActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareSpeakerActivity
import hibernate.v2.testyourandroid.ui.activity.HardwareTouchActivity
import hibernate.v2.testyourandroid.ui.activity.InfoAndroidVersionActivity
import hibernate.v2.testyourandroid.ui.activity.InfoAppTypeChooseActivity
import hibernate.v2.testyourandroid.ui.activity.InfoBatteryActivity
import hibernate.v2.testyourandroid.ui.activity.InfoBluetoothActivity
import hibernate.v2.testyourandroid.ui.activity.InfoCPUActivity
import hibernate.v2.testyourandroid.ui.activity.InfoCameraActivity
import hibernate.v2.testyourandroid.ui.activity.InfoDeviceActivity
import hibernate.v2.testyourandroid.ui.activity.InfoGSMActivity
import hibernate.v2.testyourandroid.ui.activity.InfoSystemMonitorActivity
import hibernate.v2.testyourandroid.ui.activity.InfoWifiActivity
import hibernate.v2.testyourandroid.ui.activity.SensorAccelerometerActivity
import hibernate.v2.testyourandroid.ui.activity.SensorCompassActivity
import hibernate.v2.testyourandroid.ui.activity.SensorGravityActivity
import hibernate.v2.testyourandroid.ui.activity.SensorHumidityActivity
import hibernate.v2.testyourandroid.ui.activity.SensorLightActivity
import hibernate.v2.testyourandroid.ui.activity.SensorPressureActivity
import hibernate.v2.testyourandroid.ui.activity.SensorProximityActivity
import hibernate.v2.testyourandroid.ui.activity.SensorStepActivity
import hibernate.v2.testyourandroid.ui.activity.SensorTemperatureActivity
import hibernate.v2.testyourandroid.ui.activity.ToolBubbleLevelActivity
import hibernate.v2.testyourandroid.ui.activity.ToolFlashlightActivity
import hibernate.v2.testyourandroid.ui.activity.ToolQRScannerActivity
import hibernate.v2.testyourandroid.ui.activity.ToolSoundMeterActivity
import hibernate.v2.testyourandroid.ui.activity.ToolSpeedTestActivity
import hibernate.v2.testyourandroid.ui.adapter.MainTestSection
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_main_gridview.*
import java.util.ArrayList
import java.util.Random

class MainTestFragment : BaseFragment() {

    private lateinit var sectionAdapter: SectionedRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_gridview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appUpdater = AppUpdater(context)
                .showEvery(4)
                .setDisplay(Display.NOTIFICATION)
        appUpdater.start()
        sectionAdapter = SectionedRecyclerViewAdapter()

        val toolsClassArray = arrayOf<Class<*>>(
                ToolQRScannerActivity::class.java, ToolFlashlightActivity::class.java,
                ToolBubbleLevelActivity::class.java, ToolSoundMeterActivity::class.java,
                ToolSpeedTestActivity::class.java
        )
        val hardwareClassArray = arrayOf<Class<*>>(
                HardwareScreenActivity::class.java, DrawingActivity::class.java, HardwareTouchActivity::class.java,
                HardwareCameraActivity::class.java, HardwareFingerprintActivity::class.java,
                HardwareSpeakerActivity::class.java, HardwareMicrophoneActivity::class.java,
                HardwareNFCActivity::class.java, HardwareLocationActivity::class.java
        )
        val sensorClassArray = arrayOf<Class<*>>(
                SensorStepActivity::class.java, SensorTemperatureActivity::class.java, SensorCompassActivity::class.java,
                SensorLightActivity::class.java, SensorAccelerometerActivity::class.java,
                SensorProximityActivity::class.java, SensorPressureActivity::class.java,
                SensorGravityActivity::class.java, SensorHumidityActivity::class.java
        )
        val infoClassArray = arrayOf<Class<*>>(
                InfoSystemMonitorActivity::class.java, InfoWifiActivity::class.java, InfoBluetoothActivity::class.java,
                InfoCPUActivity::class.java, InfoDeviceActivity::class.java, InfoAndroidVersionActivity::class.java,
                InfoBatteryActivity::class.java, InfoCameraActivity::class.java,
                InfoGSMActivity::class.java, InfoAppTypeChooseActivity::class.java
        )
        val otherStringArray = arrayOf(
                "rate", "language", "donate", "app_brain"
        )
        context?.let { context ->
            sectionAdapter.addSection(MainTestSection(context, getString(R.string.main_title_tools), addList(
                    resources.getStringArray(R.array.tools_string_array),
                    resources.obtainTypedArray(R.array.main_tools_image),
                    toolsClassArray
            )))
            sectionAdapter.addSection(MainTestSection(context, getString(R.string.main_title_information), addList(
                    resources.getStringArray(R.array.info_string_array),
                    resources.obtainTypedArray(R.array.main_info_image),
                    infoClassArray
            )))
            sectionAdapter.addSection(MainTestSection(context, getString(R.string.main_title_hardware), addList(
                    resources.getStringArray(R.array.hardware_string_array),
                    resources.obtainTypedArray(R.array.main_hardware_image),
                    hardwareClassArray
            )))
            sectionAdapter.addSection(MainTestSection(context, getString(R.string.main_title_sensor), addList(
                    resources.getStringArray(R.array.sensor_string_array),
                    resources.obtainTypedArray(R.array.main_sensor_image),
                    sensorClassArray
            )))
            sectionAdapter.addSection(MainTestSection(context, getString(R.string.main_title_other), addList(
                    resources.getStringArray(R.array.other_string_array),
                    otherImageArray(),
                    otherStringArray
            )))
        }

        val columnCount = if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) 4 else 3
        val gridLayoutManager = GridLayoutManager(activity, columnCount)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (sectionAdapter.getSectionItemViewType(position)) {
                    SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER -> columnCount
                    else -> 1
                }
            }
        }
        gridRv.setHasFixedSize(true)
        gridRv.layoutManager = gridLayoutManager
        gridRv.adapter = sectionAdapter
    }

    @SuppressLint("Recycle")
    private fun otherImageArray(): TypedArray {
        return when (Random().nextInt(3)) {
            0 -> resources.obtainTypedArray(R.array.main_other_image_1)
            1 -> resources.obtainTypedArray(R.array.main_other_image_2)
            else -> resources.obtainTypedArray(R.array.main_other_image_3)
        }
    }

    private fun addList(stringArray: Array<String>, imageArray: TypedArray, classArray: Array<Class<*>>): List<GridItem> {
        val list: MutableList<GridItem> = ArrayList()
        for (i in stringArray.indices) {
            list.add(GridItem(stringArray[i], imageArray.getResourceId(i, 0), classArray[i]))
        }
        return list
    }

    private fun addList(stringArray: Array<String>, imageArray: TypedArray, string2Array: Array<String>): List<GridItem> {
        val list: MutableList<GridItem> = ArrayList()
        for (i in stringArray.indices) {
            list.add(GridItem(stringArray[i], imageArray.getResourceId(i, 0), string2Array[i]))
        }
        return list
    }

    companion object {
        fun newInstance(): MainTestFragment {
            return MainTestFragment()
        }
    }
}