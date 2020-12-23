package hibernate.v2.testyourandroid.ui.base

import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.util.Utils

abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return Utils.isPermissionsGranted(context, permissions)
    }

    protected val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            var grantedAll = true
            val mutableList = mutableListOf<String>()

            permissions.entries.forEach { entry ->
                if (!entry.value) {
                    grantedAll = false
                    context?.packageManager?.let { packageManager ->
                        mutableList.add(
                            "- " + packageManager.getPermissionInfo(entry.key, 0)
                                ?.loadLabel(packageManager).toString().capitalize()
                        )
                    }
                }
            }

            if (!grantedAll) {
                Utils.openErrorPermissionDialog(context, mutableList)
            }
        }
}