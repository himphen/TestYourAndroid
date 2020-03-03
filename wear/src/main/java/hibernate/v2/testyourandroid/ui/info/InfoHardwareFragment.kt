package hibernate.v2.testyourandroid.ui.info

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
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
class InfoHardwareFragment : BaseFragment() {
    private var level = 0
    private var charge = 0
    private var health = 0

    private lateinit var arrayCharge: Array<String>
    private lateinit var arrayHealth: Array<String>
    private lateinit var adapter: InfoItemAdapter

    private var list: ArrayList<InfoItem> = arrayListOf()

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            if (charge > 4) charge = 3
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            list[2].contentText = "$level %"
            list[3].contentText = arrayHealth[health]
            list[4].contentText = arrayCharge[charge]
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    override fun onPause() {
        context?.unregisterReceiver(mBatInfoReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun init() {
        arrayCharge = resources.getStringArray(R.array.info_battery_charge_string_array)
        arrayHealth = resources.getStringArray(R.array.info_battery_health_string_array)
        list = arrayListOf()
        val stringArray = resources.getStringArray(R.array.info_hardware_string_array)
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
                0 -> Build.BRAND
                1 -> Build.DEVICE
                2 -> "$level%"
                3 -> arrayHealth[health]
                4 -> arrayCharge[charge]
                5 -> Build.VERSION.RELEASE
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}