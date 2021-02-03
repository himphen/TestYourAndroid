package hibernate.v2.testyourandroid.ui.info.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import java.util.Collections.sort

/**
 * Created by himphen on 21/5/16.
 */
class WifiAvailableFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    companion object {
        const val SCAN_WIFI_INTERVAL = 60000L
        val PERMISSION_NAME = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)

        fun newInstance(): WifiAvailableFragment {
            return WifiAvailableFragment()
        }
    }

    private lateinit var adapter: InfoItemAdapter
    private var list: MutableList<InfoItem> = mutableListOf()

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            (parentFragment as WifiFragment?)?.wifiManager?.let {
                updateScannedList(it.scanResults)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    @Suppress("DEPRECATION")
    private val scanWifiRunnable = object : Runnable {
        override fun run() {
            if ((parentFragment as WifiFragment?)?.isScanning != true) return

            (parentFragment as WifiFragment?)?.wifiManager?.startScan()

            if ((parentFragment as WifiFragment?)?.isScanning == true) {
                handler.postDelayed(this, SCAN_WIFI_INTERVAL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = InfoItemAdapter(list)
        viewBinding!!.rvlist.adapter = adapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        onStartScanning()
    }

    private fun onStartScanning() {
        if ((parentFragment as WifiFragment?)?.isScanning == true) {
            context?.let { context ->
                context.registerReceiver(
                    wifiScanReceiver, IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
                    )
                )

                handler.post(scanWifiRunnable)

                Toast.makeText(context, R.string.ui_loading, Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onPause() {
        onStopScanning()
        super.onPause()
    }

    private fun onStopScanning() {
        try {
            context?.unregisterReceiver(wifiScanReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
    }

    private fun updateScannedList(results: List<ScanResult>) {
        list.clear()
        sort(results) { lhs, rhs -> lhs.SSID.compareTo(rhs.SSID, ignoreCase = true) }
        for (result in results) {
            val ssid =
                (if (result.SSID == null || result.SSID == "") "__Hidden SSID__" else result.SSID)

            list.add(InfoItem(ssid, getScanResultText(result)))
        }
        adapter.notifyDataSetChanged()
    }

    private fun getScanResultText(result: ScanResult): String {
        var channelWidth = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (result.channelWidth) {
                ScanResult.CHANNEL_WIDTH_20MHZ -> channelWidth = "20MHZ"
                ScanResult.CHANNEL_WIDTH_40MHZ -> channelWidth = "40MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ -> channelWidth = "80MHZ"
                ScanResult.CHANNEL_WIDTH_160MHZ -> channelWidth = "160MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> channelWidth = "80MHZ + 80MHZ"
            }
        }
        val frequency = result.frequency.toString() + "MHZ"
        val level = result.level.toString() + "dBm"
        return "$frequency $level $channelWidth".trim { it <= ' ' }
    }
}