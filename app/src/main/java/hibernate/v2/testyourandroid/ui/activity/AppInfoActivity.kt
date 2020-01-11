package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.notAppFound
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.fragment.AppInfoFragment
import kotlinx.android.synthetic.main.toolbar.*

class AppInfoActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.getParcelable<AppItem>("APP")?.let {
            titleString = it.appName
            fragment = AppInfoFragment.newInstance(it)

            super.onCreate(savedInstanceState)
        } ?: run {
            setContentView(R.layout.activity_container_top_tab)
            setSupportActionBar(toolbar)
            notAppFound(this)
        }
    }
}