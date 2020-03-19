package hibernate.v2.testyourandroid.ui.app

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.getInstalledPackages
import hibernate.v2.testyourandroid.model.AppChooseItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main_info.*
import java.util.ArrayList

class AppChooseFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        // User, System, All
        val countArray = intArrayOf(0, 0, 0)
        val packageManager = context?.packageManager
        val packs = getInstalledPackages(packageManager, 0)
        for (packageInfo in packs) {
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                countArray[0]++
            } else {
                countArray[1]++
            }
            countArray[2]++
        }
        val list: MutableList<AppChooseItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.app_choose_string_array)
        val intArray = intArrayOf(
            AppListFragment.ARG_APP_TYPE_USER,
            AppListFragment.ARG_APP_TYPE_SYSTEM,
            AppListFragment.ARG_APP_TYPE_ALL
        )
        for (i in stringArray.indices) {
            list.add(
                AppChooseItem(
                    stringArray[i],
                    countArray[i].toString() + " " + getString(R.string.app_package),
                    intArray[i]
                )
            )
        }
        val mListener: AppChooseAdapter.ItemClickListener =
            object : AppChooseAdapter.ItemClickListener {
                override fun onItemDetailClick(appChooseItem: AppChooseItem) {
                    val intent = Intent(context, AppListActivity::class.java)
                    intent.putExtra(AppListFragment.ARG_APP_TYPE, appChooseItem.appType)
                    startActivity(intent)
                }
            }
        rvlist.adapter = AppChooseAdapter(list, mListener)
    }
}