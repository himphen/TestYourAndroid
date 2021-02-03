package hibernate.v2.testyourandroid.ui.appinfo

import android.os.Bundle
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity
import hibernate.v2.testyourandroid.util.Utils.notAppFound

class AppInfoActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.getParcelable<AppItem>(ARG_APP)?.let {
            titleString = it.appName
            fragment = AppInfoFragment.newInstance(it)

            super.onCreate(savedInstanceState)
        } ?: run {
            setContentView(viewBinding.root)
            setSupportActionBar(viewBinding.toolbar.root)
            notAppFound(this)
        }
    }
}