package hibernate.v2.testyourandroid.ui.info

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class InfoBluetoothActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoBluetoothFragment()
    override var titleId: Int? = R.string.title_activity_bluetooth

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.test_bluetooth, menu)
        return true
    }
}