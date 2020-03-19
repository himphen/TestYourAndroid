package hibernate.v2.testyourandroid.ui.app

import android.os.Bundle
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class AppListActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val appType =
            intent.getIntExtra(AppListFragment.ARG_APP_TYPE, AppListFragment.ARG_APP_TYPE_USER)

        fragment = AppListFragment.newInstance(appType)
        titleId = when (appType) {
            AppListFragment.ARG_APP_TYPE_USER -> R.string.title_activity_app_user
            AppListFragment.ARG_APP_TYPE_SYSTEM -> R.string.title_activity_app_system
            AppListFragment.ARG_APP_TYPE_ALL -> R.string.title_activity_app_all
            else -> R.string.title_activity_app_all
        }

        super.onCreate(savedInstanceState)
    }
}