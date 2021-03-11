package hibernate.v2.testyourandroid.util.ext

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Bitmap.rotate(degree: Int): Bitmap {
    val w = this.width
    val h = this.height
    val mtx = Matrix()
    mtx.setRotate(degree.toFloat())
    return Bitmap.createBitmap(this, 0, 0, w, h, mtx, true)
}

fun PackageInfo.isSystemPackage(): Boolean {
    return (this.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
}

fun Activity?.isPermissionsGranted(permissions: Array<String>): Boolean {
    if (this == null) return false
    
    return permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun Fragment.isPermissionsGranted(permissions: Array<String>) =
    activity.isPermissionsGranted(permissions)

/**
 * Returns true when [Context] is unavailable or is about to become unavailable
 */
fun Context?.isDoomed(): Boolean = when (this) {
    null -> true
    is Application -> false
    is Activity -> (this.isDestroyed or this.isFinishing)
    else -> false
}
