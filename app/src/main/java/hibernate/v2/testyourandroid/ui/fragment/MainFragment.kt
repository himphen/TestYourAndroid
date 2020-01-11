package hibernate.v2.testyourandroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.ui.adapter.MainFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment() {
    private var adView: AdView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adView = UtilHelper.initAdView(context, adLayout)
        val adapter = MainFragmentPagerAdapter(context!!, childFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)
        // Iterate over all tabs and set the custom view
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.customView = adapter.getTabView(i)
        }
    }

    override fun onDestroy() {
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }
}