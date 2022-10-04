package hibernate.v2.testyourandroid.ui.info.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.himphen.logger.Logger
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentViewPagerConatinerBinding
import hibernate.v2.testyourandroid.model.CurrentWifi
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted
import hibernate.v2.testyourandroid.util.tickerFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by himphen on 21/5/16.
 */
class WifiFragment : BaseFragment<FragmentViewPagerConatinerBinding>() {

    private val viewModel by sharedViewModel<WifiViewModel>()

    override val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentViewPagerConatinerBinding.inflate(inflater, container, false)

    companion object {
        const val SCAN_WIFI_INTERVAL = 60000L
        const val UPDATE_CHART_INTERVAL = 1000L
        fun newInstant() = WifiFragment()
    }

    private lateinit var adapter: WifiFragmentPagerAdapter

    var wifiManager: WifiManager? = null
    private var connectivityManager: ConnectivityManager? = null

    var isNetworkAvailable = false

    private val wifiStateChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extraWifiState = intent.getIntExtra(
                WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN
            )
            when (extraWifiState) {
                WifiManager.WIFI_STATE_DISABLED -> openFunctionDialog()
            }
        }
    }

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            wifiManager?.let { wifiManager ->
                viewModel.scanResultLiveData.postValue(wifiManager.scanResults)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = viewBinding!!
        activity?.let { activity ->
            wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE)
                as WifiManager
            connectivityManager =
                activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

            val tabTitles = resources.getStringArray(R.array.test_wifi_tab_title)
            // Note that we are passing childFragmentManager, not FragmentManager
            adapter = WifiFragmentPagerAdapter(this)
            (activity as BaseActivity<out ViewBinding>).supportActionBar?.title = tabTitles[0]
            viewBinding.viewPager.adapter = adapter
            viewBinding.viewPager.offscreenPageLimit = 2
            viewBinding.viewPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        activity.supportActionBar?.title = (tabTitles[position])
                    }
                })

            TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, position ->
                tab.customView = adapter.getTabView(position)
            }.attach()

            if (!isPermissionsGranted(permissions)) {
                permissionLifecycleObserver?.requestPermissions(permissions)
            }
        }

        initEvent()
    }

    override fun initEvent() {
        viewModel.isScanningLiveData.observe(viewLifecycleOwner) {
            if (it) {
                wifiManager?.startScan()
                wifiGetCurrent()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                tickerFlow(SCAN_WIFI_INTERVAL.milliseconds).collect {
                    wifiScanResult()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                tickerFlow(UPDATE_CHART_INTERVAL.milliseconds).collect {
                    wifiGetCurrent()
                }
            }
        }
    }

    private fun wifiScanResult() {
        Logger.t("lifecycle").d("wifiScanResult")
        viewModel.scanResultLiveData.postValue(wifiManager?.scanResults ?: listOf())
    }

    private fun wifiGetCurrent() {
        Logger.t("lifecycle").d("wifiGetCurrent")
        viewModel.currentWifiLiveData.postValue(
            CurrentWifi(
                wifiManager?.connectionInfo,
                wifiManager?.dhcpInfo
            )
        )
    }

    override fun onPause() {
        onStopScanning()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (isPermissionsGranted(permissions)) {
            onStartScanning()
        }
    }

    private fun onStartScanning() {
        isNetworkAvailable = checkNetworkAvailable()

        context?.registerReceiver(
            wifiStateChangedReceiver,
            IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION
            )
        )

        context?.registerReceiver(
            wifiScanReceiver,
            IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networkRequestBuilder = NetworkRequest.Builder()
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            connectivityManager?.registerNetworkCallback(
                networkRequestBuilder.build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        Logger.d("onAvailable: $network")
                        isNetworkAvailable = true
                    }

                    override fun onLost(network: Network) {
                        Logger.d("onLost: $network")
                        isNetworkAvailable = false
                    }
                }
            )

            viewModel.isScanningLiveData.postValue(true)
        } else {
            Toast.makeText(
                context,
                getString(R.string.ui_not_support_android_version, "5.0"),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkNetworkAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager?.activeNetwork
                ?: return false
            val actNw = connectivityManager?.getNetworkCapabilities(nw)
                ?: return false
            return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            return checkNetworkAvailableBelowApiM()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkNetworkAvailableBelowApiM(): Boolean {
        val nwInfo = connectivityManager?.activeNetworkInfo
            ?: return false
        return nwInfo.isConnected
    }

    private fun onStopScanning() {
        viewModel.isScanningLiveData.postValue(false)

        try {
            context?.unregisterReceiver(wifiStateChangedReceiver)
        } catch (ignored: IllegalArgumentException) {
        }

        try {
            context?.unregisterReceiver(wifiScanReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
    }

    fun openFunctionDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.ui_caution)
                .setMessage(R.string.wifi_enable_message)
                .setPositiveButton(R.string.wifi_enable_posbtn) { dialog, which ->
                    Utils.startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
                }
                .setNegativeButton(R.string.ui_cancel) { dialog, which ->
                    activity?.finish()
                }
                .show()
        }
    }
}
