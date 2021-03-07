package hibernate.v2.testyourandroid.ui.main.item

import android.content.Context
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.app.AppChooseActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareBiometricActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareCameraActivity
import hibernate.v2.testyourandroid.ui.hardware.HardwareDrawActivity
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
import hibernate.v2.testyourandroid.ui.info.InfoOpenGLActivity
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
import hibernate.v2.testyourandroid.util.Utils
import java.util.Random

class MainTestUtils(private val context: Context) {

    fun tools(): MutableList<Any> {
        val list = mutableListOf<Any>()

        list.add(MainTestTitleItem(context.getString(R.string.main_title_tools)))

        val stringArray = context.resources.getStringArray(R.array.tools_string_array)
        val imageArray = context.resources.obtainTypedArray(R.array.main_tools_image)

        val classArray = arrayOf<Class<*>>(
            ToolQRScannerActivity::class.java,
            ToolFlashlightActivity::class.java,
            ToolBubbleLevelActivity::class.java,
            ToolSoundMeterActivity::class.java,
            ToolSpeedTestActivity::class.java
        )
        val badgeArray = arrayOf(
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NEW
        )

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

        imageArray.recycle()

        return list
    }

    fun hardware(): MutableList<Any> {
        val list = mutableListOf<Any>()

        list.add(MainTestTitleItem(context.getString(R.string.main_title_hardware)))

        val stringArray = context.resources.getStringArray(R.array.hardware_string_array)
        val imageArray = context.resources.obtainTypedArray(R.array.main_hardware_image)

        val classArray = arrayOf<Class<*>>(
            HardwareScreenActivity::class.java,
            HardwareDrawActivity::class.java,
            HardwareTouchActivity::class.java,
            HardwareCameraActivity::class.java,
            HardwareSpeakerActivity::class.java,
            HardwareMicrophoneActivity::class.java,
            HardwareNFCActivity::class.java,
            HardwareLocationActivity::class.java,
            HardwareBiometricActivity::class.java
        )

        val badgeArray = arrayOf(
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NEW
        )

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

        imageArray.recycle()

        return list
    }

    fun info(): MutableList<Any> {
        val list = mutableListOf<Any>()

        list.add(MainTestTitleItem(context.getString(R.string.main_title_information)))

        val stringArray = context.resources.getStringArray(R.array.info_string_array)
        val imageArray = context.resources.obtainTypedArray(R.array.main_info_image)

        val classArray = arrayOf<Class<*>>(
            MonitorActivity::class.java,
            WifiActivity::class.java,
            InfoBluetoothActivity::class.java,
            InfoCPUActivity::class.java,
            InfoOpenGLActivity::class.java,
            InfoHardwareActivity::class.java,
            InfoAndroidVersionActivity::class.java,
            InfoBatteryActivity::class.java,
            InfoCameraActivity::class.java,
            InfoGSMActivity::class.java,
            AppChooseActivity::class.java
        )

        val badgeArray = arrayOf(
            GridItem.Badge.NONE,
            GridItem.Badge.NEW,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NEW,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE
        )

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

        imageArray.recycle()

        return list
    }

    fun sensor(): MutableList<Any> {
        val list = mutableListOf<Any>()

        list.add(MainTestTitleItem(context.getString(R.string.main_title_sensor)))

        val stringArray = context.resources.getStringArray(R.array.sensor_string_array)
        val imageArray = context.resources.obtainTypedArray(R.array.main_sensor_image)

        val classArray = arrayOf<Class<*>>(
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

        val badgeArray = arrayOf(
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE,
            GridItem.Badge.NONE
        )

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

        imageArray.recycle()

        return list
    }

    fun other(): MutableList<Any> {
        val list = mutableListOf<Any>()

        list.add(MainTestTitleItem(context.getString(R.string.main_title_other)))

        val stringArray = context.resources.getStringArray(R.array.other_string_array)
        val imageArray = when (Random().nextInt(3)) {
            0 -> context.resources.obtainTypedArray(R.array.main_other_image_1)
            1 -> context.resources.obtainTypedArray(R.array.main_other_image_2)
            else -> context.resources.obtainTypedArray(R.array.main_other_image_3)
        }

        val actionArray = arrayOf(
            GridItem.Action.HOME_RATE,
            GridItem.Action.HOME_LANGUAGE,
            GridItem.Action.HOME_DONATE,
            GridItem.Action.HOME_APP_BRAIN
        )

        for (i in stringArray.indices) {
            list.add(
                GridItem(
                    text = stringArray[i],
                    image = imageArray.getResourceId(i, 0),
                    action = actionArray[i]
                )
            )
        }

        imageArray.recycle()

        return list
    }

    fun addAdItem(): MainTestAdItem? {
        return if (!Utils.isAdHidden()) {
            val adView = AdView(context)
            adView.adUnitId = BuildConfig.ADMOB_HOME_AD_ID
            adView.adSize = AdSize.MEDIUM_RECTANGLE
            MainTestAdItem(adView)
        } else {
            null
        }
    }
}