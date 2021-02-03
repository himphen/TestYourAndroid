package hibernate.v2.testyourwear.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.databinding.FragmentMainBinding
import hibernate.v2.testyourwear.model.MainItem
import hibernate.v2.testyourwear.ui.base.BaseFragment
import hibernate.v2.testyourwear.ui.info.InfoAndroidVersionActivity
import hibernate.v2.testyourwear.ui.info.InfoBatteryActivity
import hibernate.v2.testyourwear.ui.info.InfoCPUActivity
import hibernate.v2.testyourwear.ui.info.InfoHardwareActivity
import hibernate.v2.testyourwear.ui.test.TestColorActivity
import hibernate.v2.testyourwear.ui.test.TestSensorAccelerometerActivity
import hibernate.v2.testyourwear.ui.test.TestSensorGravityActivity
import hibernate.v2.testyourwear.ui.test.TestSensorLightActivity
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater, container, false)

    private val imageArray = intArrayOf(
        R.drawable.ic_test_screen, R.drawable.ic_test_chip,
        R.drawable.ic_test_chip, R.drawable.ic_test_chip,
        R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info
    )
    private val classArray = arrayOf<Class<*>>(
        TestColorActivity::class.java, TestSensorLightActivity::class.java,
        TestSensorAccelerometerActivity::class.java, TestSensorGravityActivity::class.java,
        InfoCPUActivity::class.java, InfoHardwareActivity::class.java,
        InfoAndroidVersionActivity::class.java, InfoBatteryActivity::class.java
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        val stringArray = resources.getStringArray(R.array.main_test_string_array)
        val items: MutableList<MainItem> = ArrayList()
        for (i in stringArray.indices) {
            val item = MainItem(stringArray[i], imageArray[i], classArray[i])
            items.add(item)
        }
        val adapter = MainItemAdapter(items, object : MainItemAdapter.ItemClickListener {
            override fun onItemDetailClick(item: MainItem) {
                startActivity(Intent(context, item.intentClass))
            }

        })
        viewBinding!!.recyclerView.adapter = adapter
    }
}