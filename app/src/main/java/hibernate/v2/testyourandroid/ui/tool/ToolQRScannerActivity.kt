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

class ToolQRScannerActivity : BaseFragmentActivity<ActivityContainerAdviewBinding>() {
    override fun getActivityViewBinding() = ActivityContainerAdviewBinding.inflate(layoutInflater)
    override var fragment: Fragment? = ToolQRScannerFragment.newInstance()
    override var titleId: Int? = R.string.title_activity_qr_scanner
    override var isAdViewPreserveSpace: Boolean = true
    override var pinShortcut = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLauncherActivity("LAUNCH_QR_SCANNER")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
                val shortcutIntent = Intent()
                shortcutIntent.setAction("SHORTCUT_LAUNCH").data = Uri.parse("LAUNCH_QR_SCANNER")
                val shortcut = ShortcutInfoCompat.Builder(this, "qr_scanner")
                    .setShortLabel(getString(R.string.title_activity_qr_scanner))
                    .setLongLabel(getString(R.string.title_activity_qr_scanner))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_icon_qrcode))
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