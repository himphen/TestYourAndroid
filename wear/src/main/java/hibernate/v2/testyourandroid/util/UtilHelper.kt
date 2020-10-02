package hibernate.v2.testyourandroid.util

import java.text.DecimalFormat

object UtilHelper {

    fun formatBitSize(size: Long, isSuffix: Boolean = true): String {
        var temp: String
        var fSize = size / 1024 / 1024.toDouble()
        val df = DecimalFormat("##.##")
        if (isSuffix) {
            var suffix = " MB"
            if (fSize >= 1024) {
                suffix = " GB"
                fSize /= 1024.0
            }
            temp = df.format(fSize)
            temp += suffix
        } else {
            temp = df.format(fSize)
        }
        return temp
    }
}