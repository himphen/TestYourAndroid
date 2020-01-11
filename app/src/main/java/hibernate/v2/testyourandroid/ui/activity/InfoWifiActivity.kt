package hibernate.v2.testyourandroid.ui.activity

import android.view.Menu
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.InfoWifiFragment


class InfoWifiActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = InfoWifiFragment()
    override var titleId: Int? = R.string.title_activity_wifi

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.test_wifi, menu)
        return true
    }

}