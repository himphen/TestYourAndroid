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
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoHeader
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoBatteryFragment : BaseFragment() {
    private lateinit var list: ArrayList<InfoItem>
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
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,
                    0)
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(mBatInfoReceiver)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mBatInfoReceiver, IntentFilter(
                Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    private fun init() {
        list = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_battery_string_array)
        chargeString = resources.getStringArray(R.array.info_battery_charge_string_array)
        healthString = resources.getStringArray(R.array.info_battery_health_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        rvlist.adapter = adapter
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