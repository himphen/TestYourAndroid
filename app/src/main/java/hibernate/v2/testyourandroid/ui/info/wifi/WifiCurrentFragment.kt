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
import com.himphen.logger.Logger
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolWifiStrengthBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.ipAddressIntToString
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import hibernate.v2.testyourandroid.util.ext.convertDpToPx
import hibernate.v2.testyourandroid.util.ext.disableChangeAnimation
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WifiCurrentFragment : BaseFragment<FragmentToolWifiStrengthBinding>() {

    private val viewModel by sharedViewModel<WifiViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentToolWifiStrengthBinding.inflate(inflater, container, false)

    companion object {
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
        list.addAll(stringArray.mapIndexed { index, s -> InfoItem(s, "") })

        adapter = InfoItemAdapter()
        adapter.type = InfoItemAdapter.TYPE_MINIMIZED
        viewBinding.rvlist.adapter = adapter
        viewBinding.rvlist.disableChangeAnimation()
        adapter.setData(list)

        initGraphView()
        initEvent()
    }

    override fun initEvent() {
        viewModel.currentWifiLiveData.observe(viewLifecycleOwner) {
            Logger.t("lifecycle").d("viewModel.currentWifiLiveData: $it")
            val wifiInfo = it.wifiInfo
            val dhcpInfo = it.dhcpInfo
            val yValue = (it.wifiInfo?.rssi ?: -127.0).toDouble()
            if (wifiInfo != null && dhcpInfo != null) {
                val list = mutableListOf<InfoItem>()
                list.addAll(
                    stringArray.mapIndexed { index, s ->
                        InfoItem(
                            s,
                            getData(index, it.wifiInfo, dhcpInfo)
                        )
                    }
                )
                adapter.setData(list)
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
    private fun getData(j: Int, wifiInfo: WifiInfo?, dhcpInfo: DhcpInfo?): String? {
        return try {
            when (j) {
                0 -> wifiInfo?.ssid?.replace("\"", "") ?: "-"
                1 -> wifiInfo?.bssid ?: "-"
                2 -> wifiInfo?.networkId?.toString() ?: "-"
                3 -> (wifiInfo?.linkSpeed?.toString() ?: "-") + LINK_SPEED_UNITS
                4 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    (wifiInfo?.txLinkSpeedMbps?.toString() ?: "-") + LINK_SPEED_UNITS
                } else {
                    getString(R.string.ui_not_support_android_version, "10")
                }
                5 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    (wifiInfo?.rxLinkSpeedMbps?.toString() ?: "-") + LINK_SPEED_UNITS
                } else {
                    getString(R.string.ui_not_support_android_version, "10")
                }
                6 -> (wifiInfo?.rssi?.toString() ?: "-") + "dBm"
                7 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    (wifiInfo?.frequency?.toString() ?: "-") + "MHz"
                } else {
                    getString(R.string.ui_not_support_android_version, "5.0")
                }
                8 -> ipAddressIntToString(wifiInfo?.ipAddress)
                9 -> ipAddressIntToString(dhcpInfo?.gateway)
                10 -> ipAddressIntToString(dhcpInfo?.netmask)
                11 -> ipAddressIntToString(dhcpInfo?.dns1)
                12 -> ipAddressIntToString(dhcpInfo?.dns2)
                13 -> ipAddressIntToString(dhcpInfo?.serverAddress)
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}
