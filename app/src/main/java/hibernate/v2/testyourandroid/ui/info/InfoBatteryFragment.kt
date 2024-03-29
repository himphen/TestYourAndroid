package hibernate.v2.testyourandroid.ui.info

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.ext.roundTo

/**
 * Created by himphen on 21/5/16.
 */
class InfoBatteryFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    private lateinit var adapter: InfoItemAdapter
    private var health = 0
    private var level = 0
    private var charge = 0
    private var scale = 0
    private var technology = "-"
    private var voltage = 0
    private var celsiusTemperature = 0.0
    private var fahrenheitTemperature = 0.0
    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            technology = intent.extras?.getString(
                BatteryManager.EXTRA_TECHNOLOGY
            ) ?: "-"
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            celsiusTemperature = temperature.toDouble() / 10
            fahrenheitTemperature = (32 + celsiusTemperature * 9 / 5).roundTo(2)

            val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
            adapter.setData(list)
        }
    }
    private lateinit var stringArray: Array<String>
    private lateinit var chargeString: Array<String>
    private lateinit var healthString: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stringArray = resources.getStringArray(R.array.info_battery_string_array)
        chargeString = resources.getStringArray(R.array.info_battery_charge_string_array)
        healthString = resources.getStringArray(R.array.info_battery_health_string_array)
        val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        adapter = InfoItemAdapter().apply {
            setData(list)
        }
        viewBinding!!.rvlist.adapter = adapter
    }

    override fun onPause() {
        try {
            context?.unregisterReceiver(batteryReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(
            batteryReceiver,
            IntentFilter(
                Intent.ACTION_BATTERY_CHANGED
            )
        )
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> "$level %"
                1 -> "$scale %"
                2 -> healthString[health]
                3 -> chargeString[charge]
                4 -> technology
                5 -> "$celsiusTemperature C / $fahrenheitTemperature F"
                6 -> "$voltage mV"
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}
