package hibernate.v2.testyourandroid.ui.main

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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.app.AppChooseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.hardware.HardwareBiometricActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareCameraActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareLocationActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareMicrophoneActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareNFCActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareScreenActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareSpeakerActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareTouchActivity
import hibernate.v2.testyourandroid.ui.info.InfoAndroidVersionActivity
import hibernate.v2.testyourandroid.ui.info.InfoBatteryActivity
import hibernate.v2.testyourandroid.ui.info.InfoBluetoothActivity
import hibernate.v2.testyourandroid.ui.info.InfoCPUActivity
import hibernate.v2.testyourandroid.ui.info.InfoCameraActivity
import hibernate.v2.testyourandroid.ui.info.InfoGSMActivity
import hibernate.v2.testyourandroid.ui.info.InfoHardwareActivity
import hibernate.v2.testyourandroid.ui.info.monitor.MonitorActivity
import hibernate.v2.testyourandroid.ui.info.wifi.WifiActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorAccelerometerActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorCompassActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorGravityActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorHumidityActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorLightActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorPressureActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorProximityActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorStepActivity
import hibernate.v2.testyourandroid.ui.sensor.SensorTemperatureActivity
import hibernate.v2.testyourandroid.ui.tool.ToolBubbleLevelActivity
import hibernate.v2.testyourandroid.ui.tool.ToolFlashlightActivity
import hibernate.v2.testyourandroid.ui.tool.ToolQRScannerActivity
import hibernate.v2.testyourandroid.ui.tool.ToolSoundMeterActivity
import hibernate.v2.testyourandroid.ui.tool.speedtest.ToolSpeedTestActivity
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_main_gridview.*
import java.util.ArrayList
import java.util.Random

class MainTestFragment : BaseFragment() {

    private lateinit var sectionAdapter: SectionedRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_gridview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUpdater(context)
            .showEvery(4)
            .setDisplay(Display.NOTIFICATION)
            .start()
        sectionAdapter = SectionedRecyclerViewAdapter()

        val toolsBadgeArray = arrayOf(
            GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NEW
        )
        val hardwareBadgeArray = arrayOf(
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NEW
        )
        val sensorBadgeArray = arrayOf(
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE
        )
        val infoBadgeArray = arrayOf(
            GridItem.Badge.NONE, GridItem.Badge.NEW, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE,
            GridItem.Badge.NONE, GridItem.Badge.NONE, GridItem.Badge.NONE
        )
        val toolsClassArray = arrayOf<Class<*>>(
            ToolQRScannerActivity::class.java, ToolFlashlightActivity::class.java,
            ToolBubbleLevelActivity::class.java, ToolSoundMeterActivity::class.java,
            ToolSpeedTestActivity::class.java
        )
        val hardwareClassArray = arrayOf<Class<*>>(
            HardwareScreenActivity::class.java,
            DrawingActivity::class.java,
            HardwareTouchActivity::class.java,
            HardwareCameraActivity::class.java,
            HardwareSpeakerActivity::class.java,
            HardwareMicrophoneActivity::class.java,
            HardwareNFCActivity::class.java,
            HardwareLocationActivity::class.java,
            HardwareBiometricActivity::class.java
        )
        val sensorClassArray = arrayOf<Class<*>>(
            SensorStepActivity::class.java,
            SensorTemperatureActivity::class.java,
            SensorCompassActivity::class.java,
            SensorLightActivity::class.java,
            SensorAccelerometerActivity::class.java,
            SensorProximityActivity::class.java,
            SensorPressureActivity::class.java,
            SensorGravityActivity::class.java,
            SensorHumidityActivity::class.java
        )
        val infoClassArray = arrayOf<Class<*>>(
            MonitorActivity::class.java,
            WifiActivity::class.java,
            InfoBluetoothActivity::class.java,
            InfoCPUActivity::class.java,
            InfoHardwareActivity::class.java,
            InfoAndroidVersionActivity::class.java,
            InfoBatteryActivity::class.java,
            InfoCameraActivity::class.java,
            InfoGSMActivity::class.java,
            AppChooseActivity::class.java
        )
        val otherStringArray = arrayOf(
            "rate", "language", "donate", "app_brain"
        )

        sectionAdapter.addSection(
            MainTestSection(
                getString(R.string.main_title_tools), addList(
                    resources.getStringArray(R.array.tools_string_array),
                    resources.obtainTypedArray(R.array.main_tools_image),
                    toolsClassArray,
                    toolsBadgeArray
                )
            )
        )
        sectionAdapter.addSection(
            MainTestSection(
                getString(R.string.main_title_information), addList(
                    resources.getStringArray(R.array.info_string_array),
                    resources.obtainTypedArray(R.array.main_info_image),
                    infoClassArray,
                    infoBadgeArray
                )
            )
        )
        addAdSection()
        sectionAdapter.addSection(
            MainTestSection(
                getString(R.string.main_title_hardware), addList(
                    resources.getStringArray(R.array.hardware_string_array),
                    resources.obtainTypedArray(R.array.main_hardware_image),
                    hardwareClassArray,
                    hardwareBadgeArray
                )
            )
        )
        addAdSection()
        sectionAdapter.addSection(
            MainTestSection(
                getString(R.string.main_title_sensor), addList(
                    resources.getStringArray(R.array.sensor_string_array),
                    resources.obtainTypedArray(R.array.main_sensor_image),
                    sensorClassArray,
                    sensorBadgeArray
                )
            )
        )
        addAdSection()
        sectionAdapter.addSection(
            MainTestSection(
                getString(R.string.main_title_other), addList(
                    resources.getStringArray(R.array.other_string_array),
                    otherImageArray(),
                    otherStringArray
                )
            )
        )

        val columnCount = if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) 4 else 3
        val gridLayoutManager = GridLayoutManager(activity, columnCount)
        gridLayoutManager.spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (sectionAdapter.getSectionForPosition(position) is MainAdSection) {
                        return columnCount
                    }

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

    private fun addAdSection() {
        context?.let { context ->
            val adView = AdView(context)
            adView.adUnitId = BuildConfig.ADMOB_HOME_AD_ID
            adView.adSize = AdSize.MEDIUM_RECTANGLE

            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    FirebaseCrashlytics.getInstance()
                        .log("Home Ad onAdFailedToLoad errorCode: $errorCode")
                }
            }
            adView.loadAd(AdRequest.Builder().build())

            val section = MainAdSection(adView)
            section.setHasHeader(false)
            sectionAdapter.addSection(section)
        }
    }

    @SuppressLint("Recycle")
    private fun otherImageArray(): TypedArray {
        return when (Random().nextInt(3)) {
            0 -> resources.obtainTypedArray(R.array.main_other_image_1)
            1 -> resources.obtainTypedArray(R.array.main_other_image_2)
            else -> resources.obtainTypedArray(R.array.main_other_image_3)
        }
    }

    private fun addList(
        stringArray: Array<String>,
        imageArray: TypedArray,
        classArray: Array<Class<*>>,
        badgeArray: Array<GridItem.Badge>
    ): List<GridItem> {
        val list: MutableList<GridItem> = ArrayList()
        for (i in stringArray.indices) {
            list.add(
                GridItem(
                    stringArray[i],
                    imageArray.getResourceId(i, 0),
                    classArray[i],
                    badgeArray[i]
                )
            )
        }
        return list
    }

    private fun addList(
        stringArray: Array<String>,
        imageArray: TypedArray,
        string2Array: Array<String>
    ): List<GridItem> {
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