package hibernate.v2.testyourandroid.ui.activity

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.Fragment
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.fragment.ToolQRScannerFragment

class ToolQRScannerActivity : BaseFragmentActivity() {
    override var fragment: Fragment? = ToolQRScannerFragment()
    override var titleId: Int? = R.string.title_activity_qr_scanner
    override var isAdViewPreserveSpace: Boolean = true

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            val menuItem = menu.add(0, 0, 0, "Add to home screen")
            menuItem.setIcon(R.drawable.baseline_add_white_24)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
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
                val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, shortcut)
                val successCallback = PendingIntent.getBroadcast(this, 0,
                        pinnedShortcutCallbackIntent, 0)
                ShortcutManagerCompat.requestPinShortcut(
                        this, shortcut,
                        successCallback.intentSender)
            }
        }
        return true
    }
}