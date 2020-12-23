package hibernate.v2.testyourandroid.ui.info.wifi

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.testyourandroid.R

class WifiFragmentPagerAdapter(
    private val fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private val tabTitles: Array<String> =
        fragment.resources.getStringArray(R.array.test_wifi_tab_title)

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = 3

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WifiCurrentFragment.newInstance()
            1 -> WifiAvailableFragment.newInstance()
            2 -> WifiSavedFragment.newInstance()
            else -> throw RuntimeException("Unknown type")
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(fragment.context, R.layout.custom_tab, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }
}