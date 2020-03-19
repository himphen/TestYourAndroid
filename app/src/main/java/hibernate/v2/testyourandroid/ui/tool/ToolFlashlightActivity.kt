package hibernate.v2.testyourandroid.ui.tool

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolFlashlightActivity : BaseFragmentActivity() {
    override var titleId: Int? = R.string.title_activity_flashlight
    override var pinShortcut = true

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
                val shortcutIntent = Intent()
                shortcutIntent.setAction("SHORTCUT_LAUNCH").data = Uri.parse("LAUNCH_FLASHLIGHT")
                val shortcut = ShortcutInfoCompat.Builder(this, "flashlight")
                    .setShortLabel(getString(R.string.title_activity_flashlight))
                    .setLongLabel(getString(R.string.title_activity_flashlight))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_icon_flashlight))
                    .setIntent(shortcutIntent)
                    .build()
                val pinnedShortcutCallbackIntent =
                    ShortcutManagerCompat.createShortcutResultIntent(this, shortcut)
                val successCallback = PendingIntent.getBroadcast(
                    this, 0,
                    pinnedShortcutCallbackIntent, 0
                )
                ShortcutManagerCompat.requestPinShortcut(
                    this, shortcut,
                    successCallback.intentSender
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}