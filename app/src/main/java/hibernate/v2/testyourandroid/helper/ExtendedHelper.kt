package hibernate.v2.testyourandroid.helper

fun Float.roundTo(n: Int): Float {
    return "%.${n}f".format(this).toFloat()
}

fun Double.roundTo(n: Int): Double {
    return "%.${n}f".format(this).toDouble()
}