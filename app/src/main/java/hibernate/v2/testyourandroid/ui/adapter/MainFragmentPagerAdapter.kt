package hibernate.v2.testyourandroid.ui.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.MainAboutFragment
import hibernate.v2.testyourandroid.ui.fragment.MainTestFragment

class MainFragmentPagerAdapter(
        private val context: Context,
        fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles: Array<String> = context.resources.getStringArray(R.array.main_tab_title)
    /**
     * Returns the number of pages
     */
    override fun getCount(): Int = 2

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainTestFragment.newInstance()
            1 -> MainAboutFragment()
            else -> MainAboutFragment()
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(context, R.layout.custom_tab, null)
        val tv = v.findViewById<TextView>(R.id.tabTitleTv)
        tv.text = tabTitles[position]
        return v
    }

}