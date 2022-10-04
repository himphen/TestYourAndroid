package hibernate.v2.testyourandroid.ui.hardware

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Parcelable
import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class HardwareNFCActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = HardwareNFCFragment()
    override var titleId: Int? = R.string.title_activity_nfc

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_nfc, menu)
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resolveIntent(intent)
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as? Tag

            (fragment as HardwareNFCFragment).detectTagData(tag)
        }
    }
}
