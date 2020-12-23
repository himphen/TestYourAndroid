package hibernate.v2.testyourandroid.ui.appinfo

import android.os.Bundle
import hibernate.v2.testyourandroid.databinding.ActivityContainerTopTabBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity
import hibernate.v2.testyourandroid.util.Utils.notAppFound
import hibernate.v2.testyourandroid.util.viewBinding

class AppInfoActivity : BaseFragmentActivity() {

    private val binding by viewBinding(ActivityContainerTopTabBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.getParcelable<AppItem>(ARG_APP)?.let {
            titleString = it.appName
            fragment = AppInfoFragment.newInstance(it)

            super.onCreate(savedInstanceState)
        } ?: run {
            setContentView(binding.root)
            setSupportActionBar(binding.toolbar.root)
            notAppFound(this)
        }
    }
}