package hibernate.v2.testyourandroid.ui.hardware

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareNfcBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import kotlin.experimental.and

/**
 * Created by himphen on 21/5/16.
 */
class HardwareNFCFragment : BaseFragment<FragmentHardwareNfcBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHardwareNfcBinding =
        FragmentHardwareNfcBinding.inflate(inflater, container, false)

    private var nfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startSettingsActivity(context, Settings.ACTION_NFC_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            activity,
            mPendingIntent,
            null,
            null
        )
    }

    private fun init() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        nfcAdapter?.let { nfcAdapter ->
            if (!nfcAdapter.isEnabled) {
                openFunctionDialog()
            }
            // create an intent with tag data and deliver to this activity
            mPendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(
                    context,
                    javaClass
                ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                Utils.getPendingIntentFlag()
            )
        } ?: run {
            errorNoFeatureDialog(context)
        }
    }

    private fun openFunctionDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.ui_caution)
                .setMessage(R.string.nfc_enable_message)
                .setCancelable(false)
                .setPositiveButton(R.string.nfc_enable_posbtn) { dialog, which ->
                    startSettingsActivity(context, Settings.ACTION_NFC_SETTINGS)
                }
                .setNegativeButton(R.string.ui_cancel, null)
                .show()
        }
    }

    fun detectTagData(tag: Tag?) {
        if (tag == null) return

        val sb = StringBuilder()
        val id: ByteArray = tag.id
        sb.append("ID (hex): ").append(toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)
                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')
                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')
                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')
                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: java.lang.Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }

        activity?.let { activity ->
            MaterialAlertDialogBuilder(activity)
                .setMessage(sb.toString())
                .setNegativeButton(R.string.ui_cancel, null)
                .show()
        }
    }

    private fun toHex(bytes: ByteArray): String {
        val sb = java.lang.StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b = (bytes[i] and 0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

    private fun toReversedHex(bytes: ByteArray): String {
        val sb = java.lang.StringBuilder()
        for (i in bytes.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            val b = (bytes[i] and 0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
        }
        return sb.toString()
    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value = (bytes[i] and 0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun toReversedDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = (bytes[i] and 0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }
}
