package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_hardware_location.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoGSMFragment : BaseFragment() {
    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.READ_PHONE_STATE)
    }

    private var telephonyManager: TelephonyManager? = null
    private lateinit var simStateArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

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
    @SuppressLint("HardwareIds")
    private fun getData(j: Int): String {
        context?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    PERMISSION_NAME[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                telephonyManager?.let { telephonyManager ->
                    return try {
                        when (j) {
                            0 -> telephonyManager.simCountryIso
                            1 -> telephonyManager.simOperator
                            2 -> telephonyManager.simOperatorName
                            3 -> simStateArray[telephonyManager.simState]
                            4 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (telephonyManager.phoneCount > 1) {
                                    "Sim Card 1: " + telephonyManager.getImei(0) + "\nSim Card 2: " + telephonyManager.getImei(
                                        1
                                    )
                                } else {
                                    telephonyManager.imei
                                }
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (telephonyManager.phoneCount > 1) {
                                    "Sim Card 1: " + telephonyManager.getDeviceId(0) + "\nSim Card 2: " + telephonyManager.getDeviceId(
                                        1
                                    )
                                } else {
                                    telephonyManager.getDeviceId(0)
                                }
                            } else {
                                telephonyManager.deviceId
                            }
                            5 -> telephonyManager.deviceSoftwareVersion
                            6 -> telephonyManager.line1Number
                            7 -> telephonyManager.networkCountryIso
                            8 -> telephonyManager.networkOperator
                            9 -> telephonyManager.networkOperatorName
                            10 -> telephonyManager.isNetworkRoaming.toString()
                            11 -> telephonyManager.simSerialNumber
                            12 -> telephonyManager.subscriberId
                            else -> "N/A"
                        }
                    } catch (e: Exception) {
                        "N/A"
                    }
                }
            }
        }

        return "N/A"
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