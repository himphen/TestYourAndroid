package hibernate.v2.testyourandroid.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.testyourandroid.databinding.FragmentViewPagerConatinerBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment<FragmentViewPagerConatinerBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentViewPagerConatinerBinding =
        FragmentViewPagerConatinerBinding.inflate(inflater, container, false)

    private var adView: AdView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = viewBinding!!
        adView = Utils.initAdView(context, viewBinding.adLayout)
        val adapter = MainFragmentPagerAdapter(this)
        viewBinding.viewPager.adapter = adapter
        viewBinding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()
    }

    override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }
}