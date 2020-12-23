package hibernate.v2.testyourandroid.ui.info.monitor

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.testyourandroid.R

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
        val v = View.inflate(fragment.context, R.layout.custom_tab, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }

}