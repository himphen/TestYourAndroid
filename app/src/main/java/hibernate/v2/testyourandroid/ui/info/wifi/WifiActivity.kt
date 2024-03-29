package hibernate.v2.testyourandroid.ui.info.wifi

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity
import org.koin.android.ext.android.inject

class WifiActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {

    @Suppress("unused")
    val viewModel by inject<WifiViewModel>()

    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = WifiFragment.newInstant()
    override var titleId: Int? = R.string.title_activity_wifi

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.test_wifi, menu)
        return true
    }
}
