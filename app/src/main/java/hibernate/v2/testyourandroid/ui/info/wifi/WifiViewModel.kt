package hibernate.v2.testyourandroid.ui.info.wifi

import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import hibernate.v2.testyourandroid.model.CurrentWifi
import hibernate.v2.testyourandroid.ui.base.BaseViewModel

/**
 * Created by himphen on 21/5/16.
 */
class WifiViewModel : BaseViewModel() {
    val isScanningLiveData = MutableLiveData(false)
    val scanResultLiveData = MutableLiveData<List<ScanResult>>(listOf())
    val currentWifiLiveData = MutableLiveData<CurrentWifi>()
}
