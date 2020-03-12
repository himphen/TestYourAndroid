package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.notAppFound
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class AppInfoPackageFragment : BaseFragment() {

    private var packageInfo: PackageInfo? = null
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
        arguments?.getParcelable<AppItem>("APP")?.let { appItem ->
            try {
                packageInfo = context?.packageManager?.getPackageInfo(appItem.packageName!!, 0)
                val list: ArrayList<InfoItem> = ArrayList()
                val stringArray = resources.getStringArray(R.array.app_package_string_array)
                for (i in stringArray.indices) {
                    list.add(InfoItem(stringArray[i], getData(i)))
                }
                rvlist.adapter = InfoItemAdapter(list)
            } catch (e: PackageManager.NameNotFoundException) {
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
                0 -> if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) "System Package" else "User-Installed Package"
                1 -> packageInfo!!.packageName
                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo!!.longVersionCode.toString()
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo!!.versionCode.toString()
                    }
                }
                3 -> packageInfo!!.versionName
                4 -> {
                    date = Date(packageInfo!!.firstInstallTime)
                    formatter.format(date)
                }
                5 -> {
                    date = Date(packageInfo!!.lastUpdateTime)
                    formatter.format(date)
                }
                6 -> packageInfo!!.applicationInfo.dataDir
                7 -> packageInfo!!.applicationInfo.publicSourceDir
                8 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    packageInfo!!.applicationInfo.minSdkVersion.toString()
                } else {
                    getString(R.string.ui_not_support_android_version, "7.0")
                }
                9 -> packageInfo!!.applicationInfo.targetSdkVersion.toString()
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
            args.putParcelable("APP", appItem)
            fragment.arguments = args
            return fragment
        }
    }
}