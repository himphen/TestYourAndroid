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
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.logger.Logger
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_info.*

/**
 * Created by himphen on 21/5/16.
 */
class WifiFragment : BaseFragment(R.layout.fragment_view_pager_conatiner) {
    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)

        fun newInstant(): WifiFragment {
            return WifiFragment()
        }
    }

    private lateinit var adapter: WifiFragmentPagerAdapter
    private lateinit var tabTitles: Array<String>

    var wifiManager: WifiManager? = null
    private var connectivityManager: ConnectivityManager? = null

    var isScanning = false
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE)
                    as WifiManager
            connectivityManager =
                activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                        as ConnectivityManager

            tabTitles = resources.getStringArray(R.array.test_wifi_tab_title)
            // Note that we are passing childFragmentManager, not FragmentManager
            adapter = WifiFragmentPagerAdapter(activity, childFragmentManager)
            (activity as BaseActivity).supportActionBar?.title = tabTitles[0]
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 2
            tabLayout.setupWithViewPager(viewPager)
            // Iterate over all tabs and set the custom view
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i)
                tab?.customView = adapter.getTabView(i)
            }
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    activity.supportActionBar?.title = (tabTitles[position])
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })

            if (!isPermissionsGranted(PERMISSION_NAME)) {
                requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onPause() {
        onStopScanning()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (isPermissionsGranted(PERMISSION_NAME)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                onStartScanning()
            }
        }
    }

    private fun onStartScanning() {
        isNetworkAvailable = checkNetworkAvailable()

        context?.registerReceiver(
            wifiStateChangedReceiver, IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION
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

            isScanning = true
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
        isScanning = false

        try {
            context?.unregisterReceiver(wifiStateChangedReceiver)
        } catch (ignored: IllegalArgumentException) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                UtilHelper.openErrorPermissionDialog(context)
            }
        }
    }

    fun openFunctionDialog() {
        context?.let {
            MaterialDialog(it)
                .title(R.string.ui_caution)
                .message(R.string.wifi_enable_message)
                .positiveButton(R.string.wifi_enable_posbtn) {
                    UtilHelper.startSettingsActivity(context, Settings.ACTION_WIFI_SETTINGS)
                }
                .negativeButton(R.string.ui_cancel)
                .show()
        }
    }
}