package hibernate.v2.testyourandroid.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import hibernate.v2.testyourandroid.util.Utils
import java.util.Locale

abstract class BaseFragment<T : ViewBinding?> : Fragment() {
    var viewBinding: T? = null

    protected fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return Utils.isPermissionsGranted(context, permissions)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getViewBinding(inflater, container, savedInstanceState)
        return viewBinding?.root
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
                                ?.loadLabel(packageManager).toString().capitalize(Locale.getDefault())
                        )
                    }
                }
            }

            if (!grantedAll) {
                Utils.openErrorPermissionDialog(context, mutableList)
            }
        }

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T?
}