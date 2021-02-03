package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.testyourandroid.databinding.FragmentAppInfoBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.notAppFound

class AppInfoFragment : BaseFragment<FragmentAppInfoBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAppInfoBinding =
        FragmentAppInfoBinding.inflate(inflater, container, false)

    private var appItem: AppItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appItem = arguments?.getParcelable(ARG_APP)
        appItem?.let { appItem ->
            // Note that we are passing childFragmentManager, not FragmentManager
            val adapter = AppInfoFragmentPagerAdapter(this, appItem)
            viewBinding!!.viewPager.adapter = adapter
            viewBinding!!.viewPager.currentItem = 0
            viewBinding!!.viewPager.offscreenPageLimit = 2
            TabLayoutMediator(viewBinding!!.tabLayout, viewBinding!!.viewPager) { tab, position ->
                tab.customView = adapter.getTabView(position)
            }.attach()
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