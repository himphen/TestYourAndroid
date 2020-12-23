package hibernate.v2.testyourandroid.ui.appinfo

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.AppItem

class AppInfoFragmentPagerAdapter(
    private val fragment: Fragment,
    private val appItem: AppItem
) : FragmentStateAdapter(fragment) {

    private val tabTitles: Array<String> =
        fragment.resources.getStringArray(R.array.app_info_tab_title)

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = 3

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AppInfoPackageFragment.newInstance(appItem)
            2 -> AppInfoPermissionFragment.newInstance(appItem)
            0 -> AppInfoActionFragment.newInstance(appItem)
            else -> AppInfoActionFragment.newInstance(appItem)
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(fragment.context, R.layout.custom_tab_inverse, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }

}