package hibernate.v2.testyourandroid.ui.hardware

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareNfcBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity

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
    private lateinit var mIntentFilters: Array<IntentFilter>
    private lateinit var mNFCTechLists: Array<Array<String>>
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
            mIntentFilters,
            mNFCTechLists
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
            // set an intent filter for all MIME data
            val ndefIntent = IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED
            )
            try {
                ndefIntent.addDataType("*/*")
                mIntentFilters = arrayOf(ndefIntent)
            } catch (e: Exception) {
            }
            mNFCTechLists = arrayOf(arrayOf(NfcF::class.java.name))
        } ?: run {
            errorNoFeatureDialog(context)
        }

    }

    private fun openFunctionDialog() {
        context?.let {
            MaterialDialog(it)
                .title(R.string.ui_caution)
                .message(R.string.nfc_enable_message)
                .cancelable(false)
                .positiveButton(R.string.nfc_enable_posbtn) {
                    startSettingsActivity(context, Settings.ACTION_NFC_SETTINGS)
                }
                .negativeButton(R.string.ui_cancel)
                .show()
        }
    }
}