package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.pm.PackageInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.notAppFound
import hibernate.v2.testyourandroid.util.ext.isSystemPackage
import hibernate.v2.testyourandroid.util.viewBinding
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class AppInfoPackageFragment : BaseFragment(R.layout.fragment_info_listview) {

    private val binding by viewBinding(FragmentInfoListviewBinding::bind)
    private lateinit var packageInfo: PackageInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    private fun init() {
        arguments?.getParcelable<AppItem>(ARG_APP)?.let { appItem ->
            try {
                packageInfo = context?.packageManager?.getPackageInfo(appItem.packageName, 0) ?: throw Exception()
                val list: ArrayList<InfoItem> = ArrayList()
                val stringArray = resources.getStringArray(R.array.app_package_string_array)
                for (i in stringArray.indices) {
                    list.add(InfoItem(stringArray[i], getData(i)))
                }
                binding.rvlist.adapter = InfoItemAdapter(list)
            } catch (e: Exception) {
                notAppFound(activity)
            }
        } ?: run {
            notAppFound(activity)
        }
    }

    private fun getData(j: Int): String {
        return try {
            val date: Date
            val formatter = SimpleDateFormat.getDateTimeInstance()
            when (j) {
                0 -> if (packageInfo.isSystemPackage()) "System Package" else "User-Installed Package"
                1 -> packageInfo.packageName
                2 -> PackageInfoCompat.getLongVersionCode(packageInfo).toString()
                3 -> packageInfo.versionName
                4 -> {
                    date = Date(packageInfo.firstInstallTime)
                    formatter.format(date)
                }
                5 -> {
                    date = Date(packageInfo.lastUpdateTime)
                    formatter.format(date)
                }
                6 -> packageInfo.applicationInfo.dataDir
                7 -> packageInfo.applicationInfo.publicSourceDir
                8 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    packageInfo.applicationInfo.minSdkVersion.toString()
                } else {
                    getString(R.string.ui_not_support_android_version, "7.0")
                }
                9 -> packageInfo.applicationInfo.targetSdkVersion.toString()
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }

    companion object {
        fun newInstance(appItem: AppItem?): AppInfoPackageFragment {
            val fragment = AppInfoPackageFragment()
            val args = Bundle()
            args.putParcelable(ARG_APP, appItem)
            fragment.arguments = args
            return fragment
        }
    }
}