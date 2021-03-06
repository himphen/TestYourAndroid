package hibernate.v2.testyourwear.ui.info

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourwear.model.InfoHeader
import hibernate.v2.testyourwear.model.InfoItem
import hibernate.v2.testyourwear.ui.base.BaseFragment
import hibernate.v2.testyourwear.ui.base.InfoItemAdapter

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

    private lateinit var list: List<InfoItem>
    private lateinit var adapter: InfoItemAdapter

    private var health = 0
    private var level = 0
    private var charge = 0
    private var scale = 0
    private var technology = ""
    private var voltage = 0
    private var celsiusTemperature = 0.0
    private var fahrenheitTemperature = 0.0

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            technology = intent.extras?.getString(BatteryManager.EXTRA_TECHNOLOGY) ?: ""
            val temperature = intent.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                0
            )
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            celsiusTemperature = temperature.toDouble() / 10
            fahrenheitTemperature = 32 + celsiusTemperature * 9 / 5
            for (i in list.indices) {
                list[i].contentText = getData(i)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private lateinit var chargeString: Array<String>
    private lateinit var healthString: Array<String>

    override fun onPause() {
        super.onPause()
        try {
            context?.unregisterReceiver(mBatInfoReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(
            mBatInfoReceiver, IntentFilter(
                Intent.ACTION_BATTERY_CHANGED
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stringArray = resources.getStringArray(R.array.info_battery_string_array)
        chargeString = resources.getStringArray(R.array.info_battery_charge_string_array)
        healthString = resources.getStringArray(R.array.info_battery_health_string_array)
        list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        viewBinding!!.rvlist.adapter = adapter
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> "$level %"
                1 -> "$scale %"
                2 -> healthString[health]
                3 -> chargeString[charge]
                4 -> technology
                5 -> "$celsiusTemperature C /$fahrenheitTemperature F"
                6 -> "$voltage mV"
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}