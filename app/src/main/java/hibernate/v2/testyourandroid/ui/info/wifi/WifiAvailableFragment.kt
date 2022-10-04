package hibernate.v2.testyourandroid.ui.info.wifi

import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.himphen.logger.Logger
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import hibernate.v2.testyourandroid.util.getCompatSSID
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Collections.sort

/**
 * Created by himphen on 21/5/16.
 */
class WifiAvailableFragment : BaseFragment<FragmentInfoListviewBinding>() {

    private val viewModel by sharedViewModel<WifiViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = WifiAvailableFragment()
    }

    private lateinit var adapter: InfoItemAdapter
    private var list: MutableList<InfoItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = InfoItemAdapter().apply {
            setData(list)
        }
        viewBinding!!.rvlist.adapter = adapter

        initEvent()
    }

    override fun initEvent() {
        viewModel.scanResultLiveData.observe(viewLifecycleOwner) {
            Logger.t("lifecycle").d("viewModel.scanResultLiveData: $it")
            updateScannedList(it)
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

    private fun updateScannedList(results: List<ScanResult>) {
        list.clear()
        sort(results) { lhs, rhs ->
            lhs.getCompatSSID().toString()
                .compareTo(rhs.getCompatSSID().toString(), ignoreCase = true)
        }
        for (result in results) {
            var ssid = if (result.getCompatSSID().isNullOrEmpty()) {
                "__Hidden SSID__"
            } else {
                result.getCompatSSID().toString()
            }

            ssid += " - " + result.BSSID

            list.add(InfoItem(ssid, getScanResultText(result)))
        }
        adapter.setData(list)
    }

    private fun getScanResultText(result: ScanResult): String {
        var channelWidth: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (result.channelWidth) {
                ScanResult.CHANNEL_WIDTH_20MHZ -> channelWidth = "20MHZ"
                ScanResult.CHANNEL_WIDTH_40MHZ -> channelWidth = "40MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ -> channelWidth = "80MHZ"
                ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> channelWidth = "80MHZ + 80MHZ"
                ScanResult.CHANNEL_WIDTH_160MHZ -> channelWidth = "160MHZ"
                ScanResult.CHANNEL_WIDTH_320MHZ -> channelWidth = "320MHZ"
            }
        }

        var standard: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            standard = when (result.wifiStandard) {
                ScanResult.WIFI_STANDARD_11AC -> "802.11ac"
                ScanResult.WIFI_STANDARD_11AD -> "802.11ad"
                ScanResult.WIFI_STANDARD_11AX -> "802.11ax"
                ScanResult.WIFI_STANDARD_11BE -> "802.11be"
                ScanResult.WIFI_STANDARD_11N -> "802.11n"
                ScanResult.WIFI_STANDARD_LEGACY -> "802.11a/b/g"
                else -> null
            }
        }

        val frequency = result.frequency.toString() + "MHZ"
        val level = result.level.toString() + "dBm"

        var string = "$frequency | $level"

        if (channelWidth != null) {
            string += " | $channelWidth"
        }

        if (standard != null) {
            string += " | $standard"
        }

        return string.trim { it <= ' ' }
    }
}
