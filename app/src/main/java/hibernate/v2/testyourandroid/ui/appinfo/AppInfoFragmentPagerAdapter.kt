package hibernate.v2.testyourandroid.ui.appinfo

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.AppItem

class AppInfoFragmentPagerAdapter(
        private val context: Context,
        fm: FragmentManager,
        private val appItem: AppItem
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles: Array<String> = context.resources.getStringArray(R.array.app_info_tab_title)

    /**
     * Returns the number of pages
     */
    override fun getCount(): Int = 3

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> AppInfoPackageFragment.newInstance(appItem)
            2 -> AppInfoPermissionFragment.newInstance(appItem)
            0 -> AppInfoActionFragment.newInstance(appItem)
            else -> AppInfoActionFragment.newInstance(appItem)
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(context, R.layout.custom_tab_inverse, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }

}