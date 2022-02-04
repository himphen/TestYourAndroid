package hibernate.v2.testyourwear.ui.info

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
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourwear.model.InfoHeader
import hibernate.v2.testyourwear.model.InfoItem
import hibernate.v2.testyourwear.ui.base.BaseFragment
import hibernate.v2.testyourwear.ui.base.InfoItemAdapter

/**
 * Created by himphen on 21/5/16.
 */
class InfoHardwareFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    private var level = 0
    private var charge = 0
    private var health = 0

    private lateinit var arrayCharge: Array<String>
    private lateinit var arrayHealth: Array<String>
    private lateinit var adapter: InfoItemAdapter

    private lateinit var list: List<InfoItem>

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            if (charge > 4) charge = 3
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            list[2].contentText = "$level %"
            list[3].contentText = arrayHealth[health]
            list[4].contentText = arrayCharge[charge]
            adapter.notifyItemChanged(2)
            adapter.notifyItemChanged(3)
            adapter.notifyItemChanged(4)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onPause() {
        try {
            context?.unregisterReceiver(mBatInfoReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun init() {
        arrayCharge = resources.getStringArray(R.array.info_battery_charge_string_array)
        arrayHealth = resources.getStringArray(R.array.info_battery_health_string_array)
        val stringArray = resources.getStringArray(R.array.info_hardware_string_array)

        list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }

        adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        viewBinding!!.rvlist.adapter = adapter
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