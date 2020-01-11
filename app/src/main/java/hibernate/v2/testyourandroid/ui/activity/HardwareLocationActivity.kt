package hibernate.v2.testyourandroid.ui.activity

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.HardwareLocationFragment

class HardwareLocationActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = HardwareLocationFragment()
    override var titleId: Int? = R.string.title_activity_location

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_gps, menu)
        return true
    }
}