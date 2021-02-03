package hibernate.v2.testyourandroid.ui.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentMainInfoBinding
import hibernate.v2.testyourandroid.model.AppChooseItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.getInstalledPackages
import hibernate.v2.testyourandroid.util.ext.isSystemPackage

class AppChooseFragment : BaseFragment<FragmentMainInfoBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMainInfoBinding =
        FragmentMainInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // User, System, All
        val countArray = intArrayOf(0, 0, 0)
        val packageManager = context?.packageManager
        val packages = getInstalledPackages(packageManager, 0)
        for (packageInfo in packages) {
            if (!packageInfo.isSystemPackage()) {
                countArray[0]++
            } else {
                countArray[1]++
            }
            countArray[2]++
        }
        val stringArray = resources.getStringArray(R.array.app_choose_string_array)
        val intArray = intArrayOf(
            AppListFragment.ARG_APP_TYPE_USER,
            AppListFragment.ARG_APP_TYPE_SYSTEM,
            AppListFragment.ARG_APP_TYPE_ALL
        )

        val list = stringArray.mapIndexed { index, s ->
            AppChooseItem(
                s,
                countArray[index].toString() + " " + getString(R.string.app_package),
                intArray[index]
            )
        }

        viewBinding!!.rvlist.adapter =
            AppChooseAdapter(list, object : AppChooseAdapter.ItemClickListener {
                override fun onItemDetailClick(appChooseItem: AppChooseItem) {
                    val intent = Intent(context, AppListActivity::class.java)
                    intent.putExtra(AppListFragment.ARG_APP_TYPE, appChooseItem.appType)
                    startActivity(intent)
                }
            })
    }
}