package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
class ExtendedBluetoothDevice {
    var name: String? = null
    var riss: String? = null
        private set

    fun setRiss(riss: Int) {
        this.riss = "-$riss dBm"
    }
}