package hibernate.v2.testyourandroid.ui.info.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiInfo.LINK_SPEED_UNITS
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolWifiStrengthBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.getMacAddress
import hibernate.v2.testyourandroid.util.Utils.ipAddressIntToString
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity

class WifiCurrentFragment : BaseFragment<FragmentToolWifiStrengthBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentToolWifiStrengthBinding.inflate(inflater, container, false)

    companion object {
        const val UPDATE_CHART_INTERVAL = 2000L

        fun newInstance() = WifiCurrentFragment()
    }

    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0

    private lateinit var adapter: InfoItemAdapter
    private val list: MutableList<InfoItem> = mutableListOf()

    private val handler = Handler(Looper.getMainLooper())
    private val getWifiStrengthRunnable = object : Runnable {
        override fun run() {
            if ((parentFragment as WifiFragment?)?.isScanning != true) return

            var yValue = -127.0
            if ((parentFragment as WifiFragment?)?.isNetworkAvailable == true) {
                val wifiInfo = (parentFragment as WifiFragment?)?.wifiManager?.connectionInfo
                val dhcpInfo = (parentFragment as WifiFragment?)?.wifiManager?.dhcpInfo

                if (wifiInfo != null && dhcpInfo != null) {
                    yValue = wifiInfo.rssi.toDouble()

                    for (i in list.indices) {
                        list[i].contentText = getData(i, wifiInfo, dhcpInfo)
                    }

                    adapter.notifyDataSetChanged()
                }
            }

            series.appendData(DataPoint(lastXValue, yValue), true, 36)
            context?.let { context ->
                series.color = when {
                    yValue > -60 -> ContextCompat.getColor(context, R.color.lineColor4)
                    yValue > -80 -> ContextCompat.getColor(context, R.color.lineColor2)
                    else -> ContextCompat.getColor(context, R.color.lineColor1)
                }
            }
            viewBinding?.graphView?.viewport?.scrollToEnd()
            lastXValue += 1.0

            if ((parentFragment as WifiFragment?)?.isScanning == true) {
                handler.postDelayed(this, UPDATE_CHART_INTERVAL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.let { viewBinding ->
            context?.let { context ->
                val stringArray = resources.getStringArray(R.array.test_wifi_strength_string_array)

                list.addAll(stringArray.map { s -> InfoItem(s, "") })

                adapter = InfoItemAdapter(list)
                adapter.type = InfoItemAdapter.TYPE_MINIMIZED
                viewBinding.rvlist.adapter = adapter


                series.color = ContextCompat.getColor(context, R.color.lineColor3)
                series.thickness = ConvertUtils.dp2px(4f)
                viewBinding.graphView.addSeries(series)
                viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
                viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
                viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
                viewBinding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
                viewBinding.graphView.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                viewBinding.graphView.viewport.isXAxisBoundsManual = true
                viewBinding.graphView.viewport.setMinX(0.0)
                viewBinding.graphView.viewport.setMaxX(36.0)
                viewBinding.graphView.viewport.isYAxisBoundsManual = true
                viewBinding.graphView.viewport.setMinY(-100.0)
                viewBinding.graphView.viewport.setMaxY(-40.0)
                viewBinding.graphView.viewport.isScrollable = false
                viewBinding.graphView.viewport.isScalable = false
                viewBinding.graphView.gridLabelRenderer.labelFormatter =
                    object : DefaultLabelFormatter() {
                        override fun formatLabel(value: Double, isValueX: Boolean): String {
                            return if (isValueX) {
                                super.formatLabel(value, isValueX)
                            } else {
                                super.formatLabel(value, isValueX) + "dBm"
                            }
                        }
                    }
            }
        }
    }

    override fun onPause() {
        onStopScanning()
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        onStartScanning()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onStartScanning() {
        if ((parentFragment as WifiFragment?)?.isScanning == true) {
            handler.post(getWifiStrengthRunnable)
        }
    }

    private fun onStopScanning() {
        for (i in list.indices) {
            list[i].contentText = ""
        }
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("HardwareIds")
    private fun getData(j: Int, wifiInfo: WifiInfo, dhcpInfo: DhcpInfo): String? {
        return try {
            when (j) {
                0 -> wifiInfo.ssid.replace("\"", "")
                1 -> wifiInfo.bssid
                2 -> wifiInfo.networkId.toString()
                3 -> getMacAddress(wifiInfo) ?: ""
                4 -> wifiInfo.linkSpeed.toString() + LINK_SPEED_UNITS
                5 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    wifiInfo.txLinkSpeedMbps.toString() + LINK_SPEED_UNITS
                } else {
                    getString(R.string.ui_not_support_android_version, "10.0")
                }
                6 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    wifiInfo.rxLinkSpeedMbps.toString() + LINK_SPEED_UNITS
                } else {
                    getString(R.string.ui_not_support_android_version, "10.0")
                }
                7 -> wifiInfo.rssi.toString() + "dBm"
                8 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    wifiInfo.frequency.toString() + "MHz"
                } else {
                    getString(R.string.ui_not_support_android_version, "5.0")
                }
                9 -> ipAddressIntToString(wifiInfo.ipAddress)
                10 -> ipAddressIntToString(dhcpInfo.gateway)
                11 -> ipAddressIntToString(dhcpInfo.netmask)
                12 -> ipAddressIntToString(dhcpInfo.dns1)
                13 -> ipAddressIntToString(dhcpInfo.dns2)
                14 -> ipAddressIntToString(dhcpInfo.serverAddress)
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}