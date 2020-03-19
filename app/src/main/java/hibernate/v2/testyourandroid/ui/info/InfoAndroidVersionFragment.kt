package hibernate.v2.testyourandroid.ui.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList
import java.util.TimeZone

/**
 * Created by himphen on 21/5/16.
 */
class InfoAndroidVersionFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    private fun init() {
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_android_version_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        rvlist.adapter = adapter
    }

    private fun getData(j: Int): String? {
        return try {
            when (j) {
                0 -> Build.VERSION.RELEASE
                1 -> Build.VERSION.CODENAME
                2 -> Build.VERSION.INCREMENTAL
                3 -> Build.VERSION.SDK_INT.toString()
                4 -> System.getProperty("os.arch")
                5 -> System.getProperty("os.name")
                6 -> System.getProperty("os.version")
                7 -> System.getProperty("java.library.path")
                8 -> System.getProperty("java.specification.version")
                9 -> System.getProperty("java.specification.vendor")
                10 -> System.getProperty("java.specification.name")
                11 -> System.getProperty("java.vm.version")
                12 -> System.getProperty("java.vm.vendor")
                13 -> System.getProperty("java.vm.name")
                14 -> System.getProperty("java.vm.specification.version")
                15 -> System.getProperty("java.vm.specification.vendor")
                16 -> System.getProperty("java.vm.specification.name")
                17 -> System.getProperty("java.home")
                18 -> {
                    val tz = TimeZone.getDefault()
                    tz.getDisplayName(false, TimeZone.SHORT) + " " + tz.id
                }
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}