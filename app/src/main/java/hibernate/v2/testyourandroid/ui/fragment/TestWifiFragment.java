package hibernate.v2.testyourandroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;
import hibernate.v2.testyourandroid.ui.custom.TestWiFiScanReceiver;

/**
 * Created by himphen on 21/5/16.
 */
public class TestWifiFragment extends BaseFragment {

	private WifiManager wifiManager;

	private List<InfoItem> list = new ArrayList<>();

	private InfoItemAdapter adapter;

	private int extraWifiState;

	private BroadcastReceiver wiFiScanReceiver;
	private BroadcastReceiver wifiStateChangedReceiver;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private String[] currentStringArray;
	private WifiInfo wifiInfo;
	private DhcpInfo dhcpInfo;

	public TestWifiFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		init();
	}

	@Override
	public void onPause() {
		try {
			if (wiFiScanReceiver != null) {
				mContext.unregisterReceiver(wiFiScanReceiver);
			}
			if (wifiStateChangedReceiver != null) {
				mContext.unregisterReceiver(wifiStateChangedReceiver);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (wifiStateChangedReceiver != null)
			mContext.registerReceiver(wifiStateChangedReceiver, new IntentFilter(
					WifiManager.WIFI_STATE_CHANGED_ACTION));
		if (wiFiScanReceiver != null)
			mContext.registerReceiver(wiFiScanReceiver, new IntentFilter(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_reload:
				reload();
				break;
			case R.id.action_settings:
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.test_wifi_string_array);
		currentStringArray = getResources().getStringArray(R.array.test_wifi_current_string_array);
		wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

		// Register Broadcast Receiver
		try {
			wifiStateChangedReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					extraWifiState = intent.getIntExtra(
							WifiManager.EXTRA_WIFI_STATE,
							WifiManager.WIFI_STATE_UNKNOWN);
					switch (extraWifiState) {
						case WifiManager.WIFI_STATE_DISABLED:
							openWifiDialog();
							break;
					}
				}
			};
			wiFiScanReceiver = new TestWiFiScanReceiver(wifiManager);
			wifiInfo = wifiManager.getConnectionInfo();
			dhcpInfo = wifiManager.getDhcpInfo();
		} catch (Exception e) {
			C.openErrorDialog(mContext);
			return;
		}

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);

		recyclerView.setAdapter(adapter);
	}

	private void openWifiDialog() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_caution)
				.content(R.string.wifi_enable_message)
				.cancelable(false)
				.negativeText(R.string.ui_cancel)
				.positiveText(R.string.wifi_enable_posbtn)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
		dialog.show();
	}

	private void reload() {
		mContext.registerReceiver(wifiStateChangedReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));
		if (extraWifiState == WifiManager.WIFI_STATE_ENABLED) {
			boolean scanWifi = wifiManager.startScan();
			if (scanWifi) {
				list.clear();
				init();
				adapter.notifyDataSetChanged();
				Toast.makeText(mContext, R.string.wifi_reload_done,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, R.string.wifi_reload_fail,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					if (wifiInfo.getSSID() == null || wifiInfo.getSSID().equals("<unknown ssid>")) {
						return getString(R.string.wifi_no);
					}

					return currentStringArray[0] + wifiInfo.getSSID() + "\n"
							+ currentStringArray[1] + wifiInfo.getBSSID() + "\n"
							+ currentStringArray[2] + wifiInfo.getNetworkId() + "\n"
							+ currentStringArray[3] + wifiInfo.getMacAddress() + "\n"
							+ currentStringArray[4] + wifiInfo.getLinkSpeed() + " MBit/s" + "\n"
							+ currentStringArray[5] + wifiInfo.getRssi() + " dBm" + "\n"
							+ currentStringArray[6] + intToIp(wifiInfo.getIpAddress()) + "\n"
							+ currentStringArray[7] + intToIp(dhcpInfo.gateway) + "\n"
							+ currentStringArray[8] + intToIp(dhcpInfo.netmask) + "\n"
							+ currentStringArray[9] + intToIp(dhcpInfo.dns1) + "\n"
							+ currentStringArray[10] + intToIp(dhcpInfo.dns2) + "\n"
							+ currentStringArray[11] + intToIp(dhcpInfo.serverAddress);
				case 1:
					String allList = "";
					try {
						// List available networks
						List<ScanResult> results = wifiManager.getScanResults();
						for (ScanResult result : results) {
							if (result.SSID == null || result.SSID.equals(""))
								allList += "Hidden SSID" + "\n";
							else
								allList += result.SSID + "\n";
						}
						allList = allList.substring(0, allList.length() - 1);
					} catch (Exception ignored) {
					}
					return allList;
				case 2:
					String text = "";
					try {
						List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
						for (WifiConfiguration config : configs) {
							text += config.SSID + "\n";
						}

						text = text.substring(0, text.length() - 1);
					} catch (Exception ignored) {
					}
					return text;
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
