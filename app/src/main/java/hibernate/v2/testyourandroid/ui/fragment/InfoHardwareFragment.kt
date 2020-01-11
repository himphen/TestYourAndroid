package hibernate.v2.testyourandroid.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class InfoHardwareFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    private fun init() {
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_hardware_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        rvlist.adapter = adapter
    }

    @SuppressLint("HardwareIds")
    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> Build.BRAND
                1 -> Build.DEVICE
                2 -> Build.MODEL
                3 -> Build.PRODUCT
                4 -> Build.DISPLAY
                5 -> Build.FINGERPRINT
                6 -> Build.BOARD
                7 -> Build.HARDWARE
                8 -> Build.MANUFACTURER
                9 -> Build.SERIAL
                10 -> Build.USER
                11 -> Build.HOST
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}