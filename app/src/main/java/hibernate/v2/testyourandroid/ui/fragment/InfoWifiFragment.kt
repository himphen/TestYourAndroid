package hibernate.v2.testyourandroid.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.DhcpInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.startSettingsActivity
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList
import java.util.Collections.sort

/**
 * Created by himphen on 21/5/16.
 */
class InfoWifiFragment : BaseFragment() {
    private var list: MutableList<InfoItem> = ArrayList()
    private var adapter: InfoItemAdapter? = null
    private var isFirstLoading = true
    private var wifiManager: WifiManager? = null
    private val wiFiScanReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val results = wifiManager!!.scanResults
            updateScannedList(results)
        }
    }
    private val wifiStateChangedReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extraWifiState = intent.getIntExtra(
                    WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN)
            when (extraWifiState) {
                WifiManager.WIFI_STATE_DISABLED -> openFunctionDialog()
            }
        }
    }
    private lateinit var currentStringArray: Array<String>
    private var wifiInfo: WifiInfo? = null
    private var dhcpInfo: DhcpInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(PERMISSION_NAME)) {
            if (isFirstLoading) {
                reload(false)
                isFirstLoading = false
            } else {
                reload(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reload -> reload(true)
            R.id.action_settings -> startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(isToast: Boolean) {
        list = ArrayList()
        val stringArray = resources.getStringArray(R.array.test_wifi_string_array)
        currentStringArray = resources.getStringArray(R.array.test_wifi_current_string_array)
        wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            if (wifiManager == null) {
                throw Exception()
            }
            wifiInfo = wifiManager!!.connectionInfo
            dhcpInfo = wifiManager!!.dhcpInfo
        } catch (e: Exception) {
            errorNoFeatureDialog(context)
            return
        }
        context!!.registerReceiver(wifiStateChangedReceiver, IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION))
        context!!.registerReceiver(wiFiScanReceiver, IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        adapter = InfoItemAdapter(list)
        rvlist!!.adapter = adapter
        @Suppress("DEPRECATION")
        wifiManager!!.startScan()
        if (isToast) {
            Toast.makeText(context, R.string.wifi_reload_done, Toast.LENGTH_SHORT).show()
        }
    }

    private fun reload(isToast: Boolean) {
        unregisterReceiver()
        init(isToast)
    }

    private fun unregisterReceiver() {
        try {
            if (wiFiScanReceiver != null) {
                context?.unregisterReceiver(wiFiScanReceiver)
            }
            if (wifiStateChangedReceiver != null) {
                context?.unregisterReceiver(wifiStateChangedReceiver)
            }
        } catch (ignored: IllegalArgumentException) {
        }
    }

    private fun openFunctionDialog() {
        context?.let {
            MaterialDialog(it)
                    .title(R.string.ui_caution)
                    .message(R.string.wifi_enable_message)
                    .positiveButton(R.string.wifi_enable_posbtn) {
                        startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
                    }
                    .negativeButton(R.string.ui_cancel)
                    .show()
        }

    }

    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF)
    }

    @SuppressLint("HardwareIds")
    private fun getData(j: Int): String {
        return try {
            var text = StringBuilder()
            when (j) {
                0 -> {
                    if (wifiInfo!!.ssid == null || wifiInfo!!.ssid == "<unknown ssid>") {
                        getString(R.string.wifi_no)
                    } else currentStringArray[0] + wifiInfo!!.ssid + "\n" +
                            currentStringArray[1] + wifiInfo!!.bssid + "\n" +
                            currentStringArray[2] + wifiInfo!!.networkId + "\n" +
                            currentStringArray[3] + wifiInfo!!.macAddress + "\n" +
                            currentStringArray[4] + wifiInfo!!.linkSpeed + " MBit/s" + "\n" +
                            currentStringArray[5] + wifiInfo!!.rssi + " dBm" + "\n" +
                            currentStringArray[6] + intToIp(wifiInfo!!.ipAddress) + "\n" +
                            currentStringArray[7] + intToIp(dhcpInfo!!.gateway) + "\n" +
                            currentStringArray[8] + intToIp(dhcpInfo!!.netmask) + "\n" +
                            currentStringArray[9] + intToIp(dhcpInfo!!.dns1) + "\n" +
                            currentStringArray[10] + intToIp(dhcpInfo!!.dns2) + "\n" +
                            currentStringArray[11] + intToIp(dhcpInfo!!.serverAddress)
                }
                1 -> getString(R.string.ui_loading)
                2 -> {
                    try { // List saved networks
                        val configs = wifiManager!!.configuredNetworks
                        sort(configs) { lhs, rhs -> lhs.SSID.compareTo(rhs.SSID, ignoreCase = true) }
                        for (config in configs) {
                            text.append(config.SSID.replace("\"".toRegex(), "")).append("\n")
                        }
                        text = StringBuilder(if (text.length > 2) text.substring(0, text.length - 2) else text.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    text.toString().trim { it <= ' ' }
                }
                else -> "N/A"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "N/A"
        }
    }

    private fun updateScannedList(results: List<ScanResult>) {
        var text = StringBuilder()
        sort(results) { lhs, rhs -> lhs.SSID.compareTo(rhs.SSID, ignoreCase = true) }
        for (result in results) {
            text.append(getScanResultText(result))
        }
        text = StringBuilder(if (text.length > 2) text.substring(0, text.length - 2) else text.toString())
        list[1].contentText = text.toString()
        adapter!!.notifyDataSetChanged()
    }

    private fun getScanResultText(result: ScanResult): String {
        var text = ""
        text += (if (result.SSID == null || result.SSID == "") "__Hidden SSID__" else result.SSID) + "\n"
        var channelWidth = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (result.channelWidth) {
                ScanResult.CHANNEL_WIDTH_20MHZ -> channelWidth += "20MHZ"
                ScanResult.CHANNEL_WIDTH_40MHZ -> channelWidth += "40MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ -> channelWidth += "80MHZ"
                ScanResult.CHANNEL_WIDTH_160MHZ -> channelWidth += "160MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> channelWidth += "80MHZ+"
            }
        }
        val frequency = result.frequency.toString() + "MHZ"
        val level = result.level.toString() + "dBm"
        text += "$frequency $level $channelWidth".trim { it <= ' ' } + "\n\n"
        return text
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                openErrorPermissionDialog(context)
            }
        }
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}