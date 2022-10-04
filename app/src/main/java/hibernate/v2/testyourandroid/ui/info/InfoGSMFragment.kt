package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
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
class InfoGSMFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentInfoListviewBinding.inflate(inflater, container, false)

    override val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

    private var telephonyManager: TelephonyManager? = null
    val adapter = InfoItemAdapter()
    private lateinit var simStateArray: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding!!.rvlist.adapter = adapter
        simStateArray = resources.getStringArray(R.array.info_sim_status_string_array)

        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(permissions)) {
            updateList()
        }
    }

    private fun updateList() {
        context?.let { context ->
            telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val stringArray = resources.getStringArray(R.array.info_gsm_string_array)
            val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
            adapter.setData(list)
        }
    }

    private fun getData(j: Int): String {
        if (activity.isPermissionsGranted(permissions)) {
            telephonyManager?.let { telephonyManager ->
                return try {
                    when (j) {
                        0 -> telephonyManager.simCountryIso
                        1 -> telephonyManager.simOperator
                        2 -> telephonyManager.simOperatorName
                        3 -> simStateArray[telephonyManager.simState]
                        4 -> telephonyManager.deviceSoftwareVersion ?: "N/A"
                        5 -> telephonyManager.line1Number
                        6 -> telephonyManager.networkCountryIso
                        7 -> telephonyManager.networkOperator
                        8 -> telephonyManager.networkOperatorName
                        9 -> telephonyManager.isNetworkRoaming.toString()
                        10 -> getSubscriberId(telephonyManager)
                        else -> "N/A"
                    }
                } catch (e: Exception) {
                    "N/A"
                }
            }
        }

        return "N/A"
    }

    private fun getSubscriberId(telephonyManager: TelephonyManager): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getString(R.string.ui_not_support)
            }
            else -> {
                telephonyManager.subscriberId
            }
        }
    }
}
