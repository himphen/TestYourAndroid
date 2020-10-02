package hibernate.v2.testyourandroid.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.util.Utils

abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return Utils.isPermissionsGranted(context, permissions)
    }

    protected fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        return Utils.hasAllPermissionsGranted(grantResults)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }
}