package hibernate.v2.testyourandroid.ui.base

import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.helper.UtilHelper

abstract class BaseFragment : Fragment() {

    protected fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return UtilHelper.isPermissionsGranted(context, permissions)
    }

    protected fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        return UtilHelper.hasAllPermissionsGranted(grantResults)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }
}