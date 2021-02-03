package hibernate.v2.testyourandroid.ui.info.monitor

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.CustomTabBinding

class MonitorFragmentPagerAdapter(
    private val fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val tabTitles: Array<String> =
        fragment.resources.getStringArray(R.array.test_monitor_tab_title)

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> MonitorNetworkFragment()
            2 -> MonitorMemoryFragment()
            0 -> MonitorCPUFragment()
            else -> MonitorCPUFragment()
        }
    }

    fun getTabView(position: Int): View {
        val viewBinding = CustomTabBinding.inflate(LayoutInflater.from(fragment.context))
        viewBinding.tabTitleTv.text = tabTitles[position]
        return viewBinding.root
    }

}