package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoHardwareFragment : BaseFragment(R.layout.fragment_info_listview) {
    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.READ_PHONE_STATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(InfoGSMFragment.PERMISSION_NAME)) {
            init()
        }
    }

    private fun init() {
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_hardware_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        rvlist.adapter = adapter
    }

    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> Build.BRAND
                1 -> Build.DEVICE
                2 -> Build.MODEL
                3 -> Build.PRODUCT
                4 -> Build.DISPLAY
                5 -> Build.FINGERPRINT
                6 -> Build.BOARD
                7 -> Build.HARDWARE
                8 -> Build.MANUFACTURER
                9 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Build.getSerial()
                    } else {
                        Build.SERIAL
                    }
                }
                10 -> Build.USER
                11 -> Build.HOST
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
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
                Utils.openErrorPermissionDialog(context)
            }
        }
    }
}