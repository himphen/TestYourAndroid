package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_hardware_location.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoGSMFragment : BaseFragment(R.layout.fragment_info_listview) {
    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.READ_PHONE_STATE)
    }

    private var telephonyManager: TelephonyManager? = null
    private lateinit var simStateArray: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(PERMISSION_NAME)) {
            init()
        }
    }

    private fun init() {
        context?.let { context ->
            telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val list: MutableList<InfoItem> = ArrayList()
            val stringArray = resources.getStringArray(R.array.info_gsm_string_array)
            simStateArray = resources.getStringArray(R.array.info_sim_status_string_array)
            for (i in stringArray.indices) {
                list.add(InfoItem(stringArray[i], getData(i)))
            }
            val adapter = InfoItemAdapter(list)
            rvlist.adapter = adapter
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getData(j: Int): String {
        if (UtilHelper.isPermissionsGranted(context, PERMISSION_NAME)) {
            telephonyManager?.let { telephonyManager ->
                return try {
                    when (j) {
                        0 -> telephonyManager.simCountryIso
                        1 -> telephonyManager.simOperator
                        2 -> telephonyManager.simOperatorName
                        3 -> simStateArray[telephonyManager.simState]
                        4 -> getImei(telephonyManager)
                        5 -> telephonyManager.deviceSoftwareVersion ?: "N/A"
                        6 -> telephonyManager.line1Number
                        7 -> telephonyManager.networkCountryIso
                        8 -> telephonyManager.networkOperator
                        9 -> telephonyManager.networkOperatorName
                        10 -> telephonyManager.isNetworkRoaming.toString()
                        11 -> getSimSerialNumber(telephonyManager)
                        12 -> getSubscriberId(telephonyManager)
                        else -> "N/A"
                    }
                } catch (e: Exception) {
                    "N/A"
                }
            }
        }

        return "N/A"
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getSubscriberId(telephonyManager: TelephonyManager): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getString(R.string.ui_not_support)
            }
            else -> {
                telephonyManager.subscriberId
            }
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getSimSerialNumber(telephonyManager: TelephonyManager): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getString(R.string.ui_not_support)
            }
            else -> {
                telephonyManager.simSerialNumber
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getImei(telephonyManager: TelephonyManager): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getString(R.string.ui_not_support)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                if (telephonyManager.phoneCount > 1) {
                    "Sim Card 1: " + telephonyManager.getImei(0) +
                            "\nSim Card 2: " + telephonyManager.getImei(1)
                } else {
                    telephonyManager.imei
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (telephonyManager.phoneCount > 1) {
                    "Sim Card 1: " + telephonyManager.getDeviceId(0) +
                            "\nSim Card 2: " + telephonyManager.getDeviceId(1)
                } else {
                    telephonyManager.getDeviceId(0)
                }
            }
            else -> {
                telephonyManager.deviceId
            }
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
                openErrorPermissionDialog(context)
            }
        }
    }
}