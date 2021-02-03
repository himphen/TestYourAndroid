package hibernate.v2.testyourandroid.ui.tool

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerAdviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity

class ToolBubbleLevelActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = ToolBubbleLevelFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_bubble_level
    override var pinShortcut = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncherActivity("LAUNCH_BUBBLE_LEVEL")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
                val shortcutIntent = Intent()
                shortcutIntent.setAction("SHORTCUT_LAUNCH").data = Uri.parse("LAUNCH_BUBBLE_LEVEL")
                val shortcut = ShortcutInfoCompat.Builder(this, "qr_scanner")
                    .setShortLabel(getString(R.string.title_activity_bubble_level))
                    .setLongLabel(getString(R.string.title_activity_bubble_level))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_icon_bubble_level))
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