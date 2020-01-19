package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
class ExtendedBluetoothDevice(var name: String, var rssi: Int) {
    fun getRssi(): String {
        return "-$rssi dBm"
    }
}