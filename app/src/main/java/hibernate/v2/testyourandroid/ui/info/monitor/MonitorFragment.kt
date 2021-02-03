package hibernate.v2.testyourandroid.ui.info.monitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentViewPagerConatinerBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment

/**
 * Created by himphen on 21/5/16.
 */
class MonitorFragment : BaseFragment<FragmentViewPagerConatinerBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentViewPagerConatinerBinding =
        FragmentViewPagerConatinerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            val tabTitles = resources.getStringArray(R.array.test_monitor_tab_title)
            val adapter = MonitorFragmentPagerAdapter(this)
            (activity as BaseActivity<*>).supportActionBar?.title = tabTitles[0]
            viewBinding!!.viewPager.adapter = adapter
            viewBinding!!.viewPager.offscreenPageLimit = 2
            viewBinding!!.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    activity.supportActionBar?.title = (tabTitles[position])
                }
            })

            TabLayoutMediator(viewBinding!!.tabLayout, viewBinding!!.viewPager) { tab, position ->
                tab.customView = adapter.getTabView(position)
            }.attach()
        }
    }
}