package hibernate.v2.testyourandroid.ui.info.wifi

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
class WifiSavedFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance(): WifiSavedFragment {
            return WifiSavedFragment()
        }
    }

    private lateinit var adapter: InfoItemAdapter
    private val list = arrayListOf<InfoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = InfoItemAdapter(list)
        adapter.type = InfoItemAdapter.TYPE_SINGLE_LINE
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
        getSavedList()
    }

    private fun getSavedList() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getSavedListBelowApiQ()
        } else {
            list.clear()
            list.add(InfoItem(getString(R.string.ui_not_support), ""))
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun getSavedListBelowApiQ() {
        list.clear()
        (parentFragment as WifiFragment?)?.wifiManager?.let { wifiManager ->
            try { // List saved networks
                val wifiConfigurations = wifiManager.configuredNetworks
                sort(wifiConfigurations) { lhs, rhs ->
                    lhs.SSID.compareTo(
                        rhs.SSID,
                        ignoreCase = true
                    )
                }
                for (wifiConfiguration in wifiConfigurations) {
                    list.add(
                        InfoItem(
                            wifiConfiguration.SSID.replace("\"".toRegex(), "")
                        )
                    )
                }
            } catch (e: Exception) {
            }
        }
        adapter.notifyDataSetChanged()
    }
}