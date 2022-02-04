package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted

/**
 * Created by himphen on 21/5/16.
 */
class InfoHardwareFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    val adapter = InfoItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.rvlist.adapter = adapter

        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(permissions)) {
            init()
        }
    }

    private fun init() {
        val stringArray = resources.getStringArray(R.array.info_hardware_string_array)
        val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        adapter.submitList(list)
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
}
