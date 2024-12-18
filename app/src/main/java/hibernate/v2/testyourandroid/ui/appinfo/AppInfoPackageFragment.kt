package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.notAppFound
import hibernate.v2.testyourandroid.util.ext.isSystemPackage
import java.text.SimpleDateFormat
import java.util.Date

class AppInfoPackageFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    private lateinit var packageInfo: PackageInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<AppItem>(ARG_APP)?.let { appItem ->
            try {
                packageInfo = context?.packageManager?.getPackageInfo(appItem.packageName, 0)
                    ?: throw Exception()

                val stringArray = resources.getStringArray(R.array.app_package_string_array)
                val list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
                viewBinding!!.rvlist.adapter = InfoItemAdapter().apply {
                    setData(list)
                }
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
                3 -> packageInfo.versionName ?: throw Exception()
                4 -> {
                    date = Date(packageInfo.firstInstallTime)
                    formatter.format(date) ?: throw Exception()
                }

                5 -> {
                    date = Date(packageInfo.lastUpdateTime)
                    formatter.format(date) ?: throw Exception()
                }

                6 -> packageInfo.applicationInfo?.dataDir ?: throw Exception()
                7 -> packageInfo.applicationInfo?.publicSourceDir ?: throw Exception()
                8 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    packageInfo.applicationInfo?.minSdkVersion?.toString() ?: throw Exception()
                } else {
                    getString(R.string.ui_not_support_android_version, "7.0")
                }

                9 -> packageInfo.applicationInfo?.targetSdkVersion?.toString() ?: throw Exception()
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
