package hibernate.v2.testyourandroid.ui.tool

import android.content.Intent
import android.os.Bundle
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolFlashlightActivity : BaseFragmentActivity() {
    override var titleId: Int? = R.string.title_activity_flashlight

    override fun onCreate(savedInstanceState: Bundle?) {
        var autoOpen = false
        intent?.action?.let {
            when (it) {
                "SHORTCUT_LAUNCH" -> {
                    autoOpen = true
                }
                Intent.ACTION_MAIN -> {
                    intent?.categories?.forEach { category ->
                        if (category == Intent.CATEGORY_LAUNCHER) {
                            autoOpen = true
                            return@forEach
                        }
                    }
                }
                else -> {
                }
            }
        }
        fragment = ToolFlashlightFragment.newInstance(autoOpen)
        super.onCreate(savedInstanceState)
        initLauncherActivity("LAUNCH_FLASHLIGHT")
    }
}