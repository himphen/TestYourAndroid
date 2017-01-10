package hibernate.v2.testyourandroid.ui.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

public class TestWiFiScanReceiver extends BroadcastReceiver {
	private WifiManager wifiManager;
	private String allList = "";

	public TestWiFiScanReceiver(WifiManager wifiManager) {
		super();
		this.wifiManager = wifiManager;
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		try {
			List<ScanResult> results = wifiManager.getScanResults();
			ScanResult bestSignal = null;
			for (ScanResult result : results) {
				if (bestSignal == null
						|| WifiManager.compareSignalLevel(bestSignal.level,
						result.level) < 0) {
					bestSignal = result;
				}
				allList += result.toString();
			}
		} catch (Exception ignored) {
		}

		// String message = String.format(
		// "%s networks found. %s is the strongest.", results.size(),
		// bestSignal.SSID);
		// Toast.makeText(wifiDemo, message, Toast.LENGTH_LONG).show();

		// Log.d(TAG, "onReceive() message: " + message);
	}

}