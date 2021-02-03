package hibernate.v2.testyourwear.ui.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourwear.model.InfoHeader
import hibernate.v2.testyourwear.model.InfoItem
import hibernate.v2.testyourwear.ui.base.BaseFragment
import hibernate.v2.testyourwear.ui.base.InfoItemAdapter

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
        init()
    }

    private fun init() {
        val stringArray = resources.getStringArray(R.array.info_android_version_string_array)
        val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        val adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        viewBinding!!.rvlist.adapter = adapter
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