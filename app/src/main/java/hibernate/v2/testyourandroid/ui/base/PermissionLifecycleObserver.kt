package hibernate.v2.testyourandroid.ui.base

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import hibernate.v2.testyourandroid.util.Utils

class PermissionLifecycleObserver(
    private val context: Context?,
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        requestPermissions = registry.register(
            "key",
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            var grantedAll = true
            val mutableList = mutableListOf<String>()

            permissions.entries.forEach { entry ->
                if (!entry.value) {
                    grantedAll = false

                    context?.packageManager?.let { packageManager ->
                        mutableList.add(
                            "- " + packageManager.getPermissionInfo(entry.key, 0)
                                ?.loadLabel(packageManager).toString()
                                .replaceFirstChar { it.uppercase() }
                        )
                    }
                }
            }

            if (!grantedAll) {
                Utils.openErrorPermissionDialog(context, mutableList)
            }
        }
    }

    fun requestPermissions(permissions: Array<String>) {
        requestPermissions.launch(permissions)
    }
}