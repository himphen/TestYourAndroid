package hibernate.v2.testyourandroid.ui.info.monitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_info.*

/**
 * Created by himphen on 21/5/16.
 */
class MonitorFragment : BaseFragment() {
    private lateinit var tabTitles: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabTitles = resources.getStringArray(R.array.test_monitor_tab_title)
        // Note that we are passing childFragmentManager, not FragmentManager
        val adapter = MonitorFragmentPagerAdapter(context!!, childFragmentManager)
        (activity as BaseActivity?)?.supportActionBar?.title = tabTitles[0]
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)
        // Iterate over all tabs and set the custom view
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.customView = adapter.getTabView(i)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                (activity as BaseActivity?)?.supportActionBar?.title = (tabTitles[position])
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}