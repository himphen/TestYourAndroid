package hibernate.v2.testyourandroid.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.MainItem
import hibernate.v2.testyourandroid.ui.activity.InfoAndroidVersionActivity
import hibernate.v2.testyourandroid.ui.activity.InfoBatteryActivity
import hibernate.v2.testyourandroid.ui.activity.InfoCPUActivity
import hibernate.v2.testyourandroid.ui.activity.InfoHardwareActivity
import hibernate.v2.testyourandroid.ui.activity.TestColorActivity
import hibernate.v2.testyourandroid.ui.activity.TestSensorAccelerometerActivity
import hibernate.v2.testyourandroid.ui.activity.TestSensorGravityActivity
import hibernate.v2.testyourandroid.ui.activity.TestSensorLightActivity
import hibernate.v2.testyourandroid.ui.adaptor.MainItemAdaptor
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class MainFragment : BaseFragment() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val stringArray = resources.getStringArray(R.array.main_test_string_array)
        val items: MutableList<MainItem> = ArrayList()
        for (i in stringArray.indices) {
            val item = MainItem(stringArray[i], imageArray[i], classArray[i])
            items.add(item)
        }
        val adapter = MainItemAdaptor(items, object : MainItemAdaptor.ItemClickListener {
            override fun onItemDetailClick(item: MainItem) {
                startActivity(Intent(context, item.intentClass))
            }

        })
        recyclerView.adapter = adapter
    }
}