package hibernate.v2.testyourandroid.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding?> : Fragment() {
    open val permissions: Array<String>? = null
    var viewBinding: T? = null
    var permissionLifecycleObserver: PermissionLifecycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        permissions?.isNotEmpty()?.let {
            permissionLifecycleObserver = PermissionLifecycleObserver(
                context, requireActivity().activityResultRegistry
            )
            lifecycle.addObserver(permissionLifecycleObserver!!)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getViewBinding(inflater, container, savedInstanceState)
        return viewBinding?.root
    }

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T?
}