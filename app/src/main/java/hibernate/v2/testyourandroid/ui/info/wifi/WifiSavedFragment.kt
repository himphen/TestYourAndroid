package hibernate.v2.testyourandroid.ui.info.wifi

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.startSettingsActivity
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList
import java.util.Collections.sort

/**
 * Created by himphen on 21/5/16.
 */
class WifiSavedFragment : BaseFragment() {
    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)

        fun newInstance(): WifiSavedFragment {
            return WifiSavedFragment()
        }
    }

    private lateinit var adapter: InfoItemAdapter
    private var list: MutableList<InfoItem> = ArrayList()

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

        adapter = InfoItemAdapter(list)
        adapter.type = InfoItemAdapter.TYPE_SINGLE_LINE
        rvlist.adapter = adapter
        rvlist.layoutManager = LinearLayoutManager(context)
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

    @Suppress("DEPRECATION")
    private fun getSavedListBelowApiQ() {
        list.clear()
        (parentFragment as WifiFragment?)?.wifiManager?.let { wifiManager ->
            try { // List saved networks
                val wifiConfigurations = wifiManager.configuredNetworks
                sort(wifiConfigurations) { lhs, rhs -> lhs.SSID.compareTo(rhs.SSID, ignoreCase = true) }
                for (wifiConfiguration in wifiConfigurations) {
                    list.add(InfoItem(
                            wifiConfiguration.SSID.replace("\"".toRegex(), "")
                    ))
                }
            } catch (e: Exception) {
            }
        }
        adapter.notifyDataSetChanged()
    }
}