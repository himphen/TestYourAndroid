package hibernate.v2.testyourandroid.util.ext

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.Matrix

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