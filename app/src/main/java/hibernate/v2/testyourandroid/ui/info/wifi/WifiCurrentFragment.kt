package hibernate.v2.testyourandroid.ui.info.wifi

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiInfo.LINK_SPEED_UNITS
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import hibernate.v2.testyourandroid.util.ext.convertDpToPx
import hibernate.v2.testyourandroid.util.ext.disableChangeAnimation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

    private lateinit var stringArray: Array<String>
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0

    private lateinit var adapter: InfoItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = viewBinding!!

        stringArray = resources.getStringArray(R.array.test_wifi_strength_string_array)

        val list = mutableListOf<InfoItem>()
        list.addAll(stringArray.mapIndexed { index, s -> InfoItem(s, "", index) })

        adapter = InfoItemAdapter()
        adapter.type = InfoItemAdapter.TYPE_MINIMIZED
        viewBinding.rvlist.adapter = adapter
        viewBinding.rvlist.disableChangeAnimation()
        adapter.submitList(list)

        initGraphView()
        initEvent()
    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                try {
                    while (isActive) {
                        val wifiFragment =
                            parentFragment as? WifiFragment ?: return@repeatOnLifecycle

                        if (!wifiFragment.isScanning) return@repeatOnLifecycle

                        var yValue = -127.0
                        if (wifiFragment.isNetworkAvailable) {
                            val wifiInfo = wifiFragment.wifiManager?.connectionInfo
                            val dhcpInfo = wifiFragment.wifiManager?.dhcpInfo

                            if (wifiInfo != null && dhcpInfo != null) {
                                yValue = wifiInfo.rssi.toDouble()

                                val list = mutableListOf<InfoItem>()
                                list.addAll(
                                    stringArray.mapIndexed { index, s ->
                                        InfoItem(
                                            s,
                                            getData(index, wifiInfo, dhcpInfo),
                                            index
                                        )
                                    }
                                )

                                adapter.submitList(list)
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

                        delay(UPDATE_CHART_INTERVAL)
                    }
                } catch (ex: CancellationException) {
                    throw ex
                }
            }
        }
    }

    private fun initGraphView() {
        val viewBinding = viewBinding!!
        val context = requireContext()

        series.color = ContextCompat.getColor(context, R.color.lineColor3)
        series.thickness = context.convertDpToPx(4)
        viewBinding.graphView.addSeries(series)
        viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
        viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
        viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
        viewBinding.graphView.gridLabelRenderer.padding = context.convertDpToPx(10)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                    getString(R.string.ui_not_support_android_version, "10")
                }
                6 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    wifiInfo.rxLinkSpeedMbps.toString() + LINK_SPEED_UNITS
                } else {
                    getString(R.string.ui_not_support_android_version, "10")
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
