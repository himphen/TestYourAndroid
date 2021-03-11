package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.ExtendedBluetoothDevice
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.ui.base.PermissionLifecycleObserver
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoBluetoothFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    private val scannedList: MutableList<ExtendedBluetoothDevice> = ArrayList()
    private lateinit var list: List<InfoItem>
    private var adapter: InfoItemAdapter? = null
    private var isFirstLoading = true
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val bluetoothChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    val rssi =
                        intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                    val extendedBluetoothDevice = ExtendedBluetoothDevice(
                        name = if (device.name == null) device.address else device.name,
                        rssi = rssi
                    )
                    updateScannedList(extendedBluetoothDevice)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        permissionLifecycleObserver = PermissionLifecycleObserver(
            context, requireActivity().activityResultRegistry
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(permissions)) {
            if (isFirstLoading) {
                reload(false)
                isFirstLoading = false
            } else {
                reload(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reload -> reload(true)
            R.id.action_settings -> startSettingsActivity(
                context,
                Settings.ACTION_BLUETOOTH_SETTINGS
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(isToast: Boolean) {
        list = ArrayList()
        scannedList.clear()
        val stringArray = resources.getStringArray(R.array.test_bluetooth_string_array)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Register Broadcast Receiver
        bluetoothAdapter?.let { bluetoothAdapter ->
            if (!bluetoothAdapter.isEnabled) {
                openBluetoothDialog()
                return
            }

            context?.registerReceiver(
                bluetoothChangedReceiver, IntentFilter(
                    BluetoothDevice.ACTION_FOUND
                )
            )
            bluetoothAdapter.startDiscovery()
            list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
            adapter = InfoItemAdapter(list)
            viewBinding?.rvlist?.adapter = adapter
            if (isToast) {
                Toast.makeText(context, R.string.wifi_reload_done, Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            errorNoFeatureDialog(context)
            return
        }
    }

    private fun reload(isToast: Boolean) {
        unregisterReceiver()
        init(isToast)
    }

    private fun unregisterReceiver() {
        try {
            bluetoothChangedReceiver.let { bluetoothChangedReceiver ->
                context?.unregisterReceiver(bluetoothChangedReceiver)
            }
        } catch (ignored: IllegalArgumentException) {
        }
    }

    private fun openBluetoothDialog() {
        context?.let {
            MaterialDialog(it)
                .title(R.string.ui_caution)
                .message(R.string.bluetooth_enable_message)
                .positiveButton(R.string.bluetooth_enable_posbtn) {
                    startSettingsActivity(context, Settings.ACTION_BLUETOOTH_SETTINGS)
                }
                .negativeButton(R.string.ui_cancel) {
                    activity?.finish()
                }
                .show()
        }
    }

    private fun getData(j: Int): String {
        return try {
            var text = StringBuilder()
            when (j) {
                0 -> getString(R.string.ui_loading)
                1 -> {
                    try { // List paired devices
                        val pairedDevices = bluetoothAdapter?.bondedDevices
                        pairedDevices?.let {
                            for (result in pairedDevices) {
                                text.append(result.name).append("\n")
                            }
                        }
                        text = StringBuilder(
                            if (text.length > 1) text.substring(
                                0,
                                text.length - 1
                            ) else text.toString()
                        )
                    } catch (ignored: Exception) {
                    }
                    text.toString()
                }
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun updateScannedList(device: ExtendedBluetoothDevice) {
        scannedList.add(device)
        scannedList.sortWith { lhs, rhs ->
            lhs.name.compareTo(
                rhs.name,
                ignoreCase = true
            )
        }
        var text = StringBuilder()
        for (item in scannedList) {
            text.append(getScanResultText(item))
        }
        text = StringBuilder(
            if (text.length > 2) text.substring(
                0,
                text.length - 2
            ) else text.toString()
        )
        list[0].contentText = text.toString()
        adapter?.notifyDataSetChanged()
    }

    private fun getScanResultText(device: ExtendedBluetoothDevice): String {
        return device.name + "\n" + device.getRssi() + "\n\n"
    }
}