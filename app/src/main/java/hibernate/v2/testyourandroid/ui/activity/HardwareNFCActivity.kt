package hibernate.v2.testyourandroid.ui.activity

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.HardwareNFCFragment

class HardwareNFCActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = HardwareNFCFragment()
    override var titleId: Int? = R.string.title_activity_nfc

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_nfc, menu)
        return true
    }
}