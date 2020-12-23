package hibernate.v2.testyourandroid.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentMainBinding
import hibernate.v2.testyourandroid.model.MainItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.info.InfoAndroidVersionActivity
import hibernate.v2.testyourandroid.ui.info.InfoBatteryActivity
import hibernate.v2.testyourandroid.ui.info.InfoCPUActivity
import hibernate.v2.testyourandroid.ui.info.InfoHardwareActivity
import hibernate.v2.testyourandroid.ui.test.TestColorActivity
import hibernate.v2.testyourandroid.ui.test.TestSensorAccelerometerActivity
import hibernate.v2.testyourandroid.ui.test.TestSensorGravityActivity
import hibernate.v2.testyourandroid.ui.test.TestSensorLightActivity
import hibernate.v2.testyourandroid.util.viewBinding
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val binding by viewBinding(FragmentMainBinding::bind)

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
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
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
        binding.recyclerView.adapter = adapter
    }
}