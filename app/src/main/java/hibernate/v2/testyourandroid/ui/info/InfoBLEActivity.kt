package hibernate.v2.testyourandroid.ui.info

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class InfoBLEActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = InfoBleFragment()
    override var titleId: Int? = R.string.title_activity_ble

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_bluetooth, menu)
        return true
    }
}
