package hibernate.v2.testyourandroid.ui.appinfo

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.StringUtils
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.AppPermissionItem
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.notAppFound
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap

class AppInfoPermissionFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val map = HashMap<String, ArrayList<AppPermissionItem>>()

        arguments?.getParcelable<AppItem>(ARG_APP)?.packageName?.let { packageName ->
            context?.let { context ->
                val list: MutableList<InfoItem> = ArrayList()
                try {
                    val packageManager = context.packageManager
                    val packageInfo =
                        packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                    /* Get Permissions */
                    val requestedPermissions = packageInfo.requestedPermissions
                    val noGroupLabel = "Ungrouped Permissions"
                    if (requestedPermissions != null) {
                        for (requestedPermission in requestedPermissions) {
                            var permissionGroupLabel = noGroupLabel
                            var permissionLabel: String
                            try {
                                val permissionInfo =
                                    packageManager.getPermissionInfo(requestedPermission, 0)

                                try {
                                    val permissionGroupInfo = packageManager.getPermissionGroupInfo(
                                        permissionInfo.group ?: "", 0
                                    )
                                    permissionGroupLabel =
                                        permissionGroupInfo.loadLabel(packageManager).toString()
                                } catch (ignored: PackageManager.NameNotFoundException) {
                                } catch (ignored: NullPointerException) {
                                }
                                permissionLabel =
                                    permissionInfo.loadLabel(packageManager).toString()
                            } catch (e: Exception) {
                                permissionLabel = requestedPermission
                            }
                            permissionLabel = StringUtils.upperFirstLetter(permissionLabel)
                            val arrayList: ArrayList<AppPermissionItem> =
                                map[permissionGroupLabel] ?: arrayListOf()
                            arrayList.add(AppPermissionItem(permissionLabel))
                            map[permissionGroupLabel] = arrayList
                        }
                    }
                    val sortedKeys = ArrayList(map.keys)
                    sortedKeys.sort()
                    sortedKeys.remove(noGroupLabel)
                    sortedKeys.add(noGroupLabel)
                    for (key in sortedKeys) {
                        val permissionGroupLabel = key as String
                        val value = map[permissionGroupLabel]
                        value?.let {
                            value.sortWith(Comparator { item1, item2 ->
                                item1.permissionLabel.compareTo(
                                    item2.permissionLabel
                                )
                            })
                            val permissionLabel = StringBuilder()
                            for (s in value) {
                                permissionLabel.append(s.permissionLabel).append("\n")
                            }
                            list.add(
                                InfoItem(
                                    permissionGroupLabel,
                                    permissionLabel.toString().trim { it <= ' ' })
                            )
                        }
                    }
                } catch (e: Exception) {
                    list.add(InfoItem("Fail to fetch the permissions", "Error: -1034"))
                }
                viewBinding!!.rvlist.adapter = InfoItemAdapter(list)
            }
        } ?: run {
            notAppFound(activity)
        }
    }

    companion object {
        fun newInstance(appItem: AppItem?): AppInfoPermissionFragment {
            return AppInfoPermissionFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_APP, appItem) }
            }
        }
    }
}