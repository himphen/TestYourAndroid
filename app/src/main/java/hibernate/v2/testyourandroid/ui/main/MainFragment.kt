package hibernate.v2.testyourandroid.ui.main

import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_view_pager_conatiner.*

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment(R.layout.fragment_view_pager_conatiner) {
    private var adView: AdView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adView = Utils.initAdView(context, adLayout)
        context?.let { context ->
            val adapter = MainFragmentPagerAdapter(context, childFragmentManager)
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 2
            tabLayout.setupWithViewPager(viewPager)
            // Iterate over all tabs and set the custom view
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.customView = adapter.getTabView(i)
            }
        }
    }

    override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }
}