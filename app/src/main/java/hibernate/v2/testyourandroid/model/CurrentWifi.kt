package hibernate.v2.testyourandroid.model

import android.net.DhcpInfo
import android.net.wifi.WifiInfo

data class CurrentWifi(
    val wifiInfo: WifiInfo?,
    val dhcpInfo: DhcpInfo?
)
