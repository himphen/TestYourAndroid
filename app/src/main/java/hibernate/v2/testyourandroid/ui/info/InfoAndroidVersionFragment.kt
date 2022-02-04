package hibernate.v2.testyourandroid.ui.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import java.util.TimeZone

/**
 * Created by himphen on 21/5/16.
 */
class InfoAndroidVersionFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = InfoItemAdapter()
        viewBinding!!.rvlist.adapter = adapter

        val stringArray = resources.getStringArray(R.array.info_android_version_string_array)
        val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        adapter.submitList(list)
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
