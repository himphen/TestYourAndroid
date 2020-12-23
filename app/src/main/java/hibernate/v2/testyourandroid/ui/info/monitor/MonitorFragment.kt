package hibernate.v2.testyourandroid.ui.info.monitor

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentViewPagerConatinerBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
class MonitorFragment : BaseFragment(R.layout.fragment_view_pager_conatiner) {

    private val binding by viewBinding(FragmentViewPagerConatinerBinding::bind)
    private lateinit var tabTitles: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            tabTitles = resources.getStringArray(R.array.test_monitor_tab_title)
            val adapter = MonitorFragmentPagerAdapter(this)
            (activity as BaseActivity).supportActionBar?.title = tabTitles[0]
            binding.viewPager.adapter = adapter
            binding.viewPager.offscreenPageLimit = 2
            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    activity.supportActionBar?.title = (tabTitles[position])
                }
            })

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.customView = adapter.getTabView(position)
            }.attach()
        }
    }
}