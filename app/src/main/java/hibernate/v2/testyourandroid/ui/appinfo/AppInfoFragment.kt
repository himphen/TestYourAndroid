package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.util.Utils.notAppFound
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_info.*

class AppInfoFragment : BaseFragment(R.layout.fragment_app_info) {
    private var appItem: AppItem? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        appItem = arguments?.getParcelable(ARG_APP)
        appItem?.let { appItem ->
            context?.let {
                // Note that we are passing childFragmentManager, not FragmentManager
                val adapter = AppInfoFragmentPagerAdapter(it, childFragmentManager, appItem)
                viewPager.adapter = adapter
                viewPager.currentItem = 0
                viewPager.offscreenPageLimit = 2
                tabLayout.setupWithViewPager(viewPager)
                // Iterate over all tabs and set the custom view
                for (i in 0 until tabLayout.tabCount) {
                    val tab = tabLayout.getTabAt(i)
                    if (tab != null) {
                        tab.customView = adapter.getTabView(i)
                    }
                }
            }
        } ?: run {
            Toast.makeText(context, R.string.app_not_found, Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            appItem?.packageName?.let {
                context?.packageManager?.getPackageInfo(it, PackageManager.GET_ACTIVITIES)
            } ?: run {
                notAppFound(activity)
            }
        } catch (e: Exception) {
            notAppFound(activity)
        }
    }

    companion object {
        const val ARG_APP = "APP"

        fun newInstance(appItem: AppItem?): AppInfoFragment {
            val fragment = AppInfoFragment()
            val args = Bundle()
            args.putParcelable(ARG_APP, appItem)
            fragment.arguments = args
            return fragment
        }
    }
}