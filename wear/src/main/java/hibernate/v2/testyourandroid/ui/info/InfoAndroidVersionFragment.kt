package hibernate.v2.testyourandroid.ui.info

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoHeader
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoAndroidVersionFragment : BaseFragment(R.layout.fragment_info_listview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(activity)
        init()
    }

    private fun init() {
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_android_version_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        rvlist.adapter = adapter
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> Build.VERSION.RELEASE
                1 -> Build.VERSION.CODENAME
                2 -> Build.VERSION.INCREMENTAL
                3 -> Build.VERSION.SDK_INT.toString()
                4 -> System.getProperty("os.arch") ?: ""
                5 -> System.getProperty("os.name") ?: ""
                6 -> System.getProperty("os.version") ?: ""
                7 -> Build.BOARD
                8 -> Build.getRadioVersion()
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}