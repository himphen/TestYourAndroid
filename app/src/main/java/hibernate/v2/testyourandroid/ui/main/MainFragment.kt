package hibernate.v2.testyourandroid.ui.main

import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentViewPagerConatinerBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment(R.layout.fragment_view_pager_conatiner) {

    private val binding by viewBinding(FragmentViewPagerConatinerBinding::bind)

    private var adView: AdView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adView = Utils.initAdView(context, binding.adLayout)
        val adapter = MainFragmentPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()
    }

    override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }
}