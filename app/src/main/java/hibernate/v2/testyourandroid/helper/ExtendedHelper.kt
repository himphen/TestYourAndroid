package hibernate.v2.testyourandroid.helper

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

fun Float.roundTo(n: Int): Float {
    return this.toDouble().roundTo(n).toFloat()
}

fun Double.roundTo(n: Int): Double {
    if (this.isNaN()) return 0.0

    return try {
        BigDecimal(this).setScale(n, RoundingMode.HALF_EVEN).toDouble()
    } catch (e: NumberFormatException) {
        this.roundToInt().toDouble()
    }
}