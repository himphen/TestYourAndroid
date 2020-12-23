package hibernate.v2.testyourandroid.ui.main

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.os.Bundle
import android.view.View
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
import hibernate.v2.testyourandroid.databinding.FragmentMainGridviewBinding
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
import hibernate.v2.testyourandroid.ui.main.item.MainTestAdItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestTitleItem
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
import hibernate.v2.testyourandroid.util.Utils.isAdHidden
import hibernate.v2.testyourandroid.util.viewBinding
import java.util.Random

class MainTestFragment : BaseFragment(R.layout.fragment_main_gridview) {

    private val binding by viewBinding(FragmentMainGridviewBinding::bind)

    private lateinit var adapter: MainTestAdapter
    private var list = mutableListOf<Any>()
    private var adCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUpdater(context)
            .showEvery(4)
            .setDisplay(Display.NOTIFICATION)
            .start()

        addTestSectionItem()
        if (!isAdHidden(context)) {
            loadBannerAd(0)
        }

        val columnCount = if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) 4 else 3
        val gridLayoutManager = GridLayoutManager(activity, columnCount)
        gridLayoutManager.spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (list[position]) {
                        is MainTestAdItem,
                        is MainTestTitleItem -> columnCount
                        else -> 1
                    }
                }
            }
        binding.gridRv.setHasFixedSize(true)
        binding.gridRv.layoutManager = gridLayoutManager
        adapter = MainTestAdapter(list)
        binding.gridRv.adapter = adapter
    }

    private fun addTestSectionItem() {
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
        val actionArray = arrayOf(
            GridItem.Action.HOME_RATE,
            GridItem.Action.HOME_LANGUAGE,
            GridItem.Action.HOME_DONATE,
            GridItem.Action.HOME_APP_BRAIN
        )

        addIconItem(
            getString(R.string.main_title_tools),
            resources.getStringArray(R.array.tools_string_array),
            resources.obtainTypedArray(R.array.main_tools_image),
            toolsClassArray,
            toolsBadgeArray
        )
        addIconItem(
            getString(R.string.main_title_information),
            resources.getStringArray(R.array.info_string_array),
            resources.obtainTypedArray(R.array.main_info_image),
            infoClassArray,
            infoBadgeArray
        )
        addIconItem(
            getString(R.string.main_title_hardware),
            resources.getStringArray(R.array.hardware_string_array),
            resources.obtainTypedArray(R.array.main_hardware_image),
            hardwareClassArray,
            hardwareBadgeArray
        )
        addIconItem(
            getString(R.string.main_title_sensor),
            resources.getStringArray(R.array.sensor_string_array),
            resources.obtainTypedArray(R.array.main_sensor_image),
            sensorClassArray,
            sensorBadgeArray
        )
        addOtherIconItem(
            getString(R.string.main_title_other),
            resources.getStringArray(R.array.other_string_array),
            otherImageArray(),
            actionArray
        )
    }

    private fun loadBannerAd(index: Int) {
        if (index >= list.size) return

        val item = list[index]
        if (item !is MainTestAdItem) {
            loadBannerAd(index + 1)
            return
        }

        item.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                loadBannerAd(index + 1)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                FirebaseCrashlytics.getInstance()
                    .log("Home Adview onAdFailedToLoad errorCode: $errorCode")
                loadBannerAd(index + 1)
            }
        }
        item.adView.loadAd(AdRequest.Builder().build())
    }

    @SuppressLint("Recycle")
    private fun otherImageArray(): TypedArray {
        return when (Random().nextInt(3)) {
            0 -> resources.obtainTypedArray(R.array.main_other_image_1)
            1 -> resources.obtainTypedArray(R.array.main_other_image_2)
            else -> resources.obtainTypedArray(R.array.main_other_image_3)
        }
    }

    private fun addIconItem(
        title: String,
        stringArray: Array<String>,
        imageArray: TypedArray,
        classArray: Array<Class<*>>,
        badgeArray: Array<GridItem.Badge>
    ) {
        list.add(MainTestTitleItem(title))
        for (i in stringArray.indices) {
            list.add(
                GridItem(
                    text = stringArray[i],
                    image = imageArray.getResourceId(i, 0),
                    intentClass = classArray[i],
                    badge = badgeArray[i]
                )
            )
        }
        addAdItem()
    }

    private fun addOtherIconItem(
        title: String,
        stringArray: Array<String>,
        imageArray: TypedArray,
        actionArray: Array<GridItem.Action>
    ) {
        list.add(MainTestTitleItem(title))
        for (i in stringArray.indices) {
            list.add(
                GridItem(
                    text = stringArray[i],
                    image = imageArray.getResourceId(i, 0),
                    action = actionArray[i]
                )
            )
        }
    }

    private fun addAdItem() {
        context?.let { context ->
            if (!isAdHidden(context)) {
                if (adCount++ < ITEMS_PER_AD) return
                val adView = AdView(context)
                adView.adUnitId = BuildConfig.ADMOB_HOME_AD_ID
                adView.adSize = AdSize.MEDIUM_RECTANGLE
                list.add(
                    MainTestAdItem(
                        adView
                    )
                )
                adCount = 0
            }
        }
    }

    companion object {
        const val ITEMS_PER_AD = 1
        fun newInstance(): MainTestFragment {
            return MainTestFragment()
        }
    }

    override fun onResume() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.resume()
        }
        super.onResume()
    }

    override fun onPause() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.destroy()
        }
        super.onDestroy()
    }
}