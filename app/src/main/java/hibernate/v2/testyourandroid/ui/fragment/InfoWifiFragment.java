package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoWifiFragment extends BaseFragment {

	protected final String[] PERMISSION_NAME = {Manifest.permission.ACCESS_COARSE_LOCATION};

	private List<InfoItem> list = new ArrayList<>();
	private InfoItemAdapter adapter;
	private boolean isFirstLoading = true;

	private WifiManager wifiManager;
	private BroadcastReceiver wiFiScanReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			List<ScanResult> results = wifiManager.getScanResults();
			updateScannedList(results);
		}
	};
	private BroadcastReceiver wifiStateChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int extraWifiState = intent.getIntExtra(
					WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN);
			switch (extraWifiState) {
				case WifiManager.WIFI_STATE_DISABLED:
					openWifiDialog();
					break;
			}
		}
	};

	private String[] currentStringArray;
	private WifiInfo wifiInfo;
	private DhcpInfo dhcpInfo;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	public InfoWifiFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isPermissionsGranted(PERMISSION_NAME)) {
			if (isFirstLoading) {
				reload(false);
				isFirstLoading = false;
			} else {
				reload(true);
			}
		} else {
			requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_reload:
				reload(true);
				break;
			case R.id.action_settings:
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(boolean isToast) {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.test_wifi_string_array);
		currentStringArray = getResources().getStringArray(R.array.test_wifi_current_string_array);
		wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		try {
			if (wifiManager == null) {
				throw new Exception();
			}
			wifiInfo = wifiManager.getConnectionInfo();
			dhcpInfo = wifiManager.getDhcpInfo();
		} catch (Exception e) {
			C.errorNoFeatureDialog(mContext);
			return;
		}

		mContext.registerReceiver(wifiStateChangedReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));

		mContext.registerReceiver(wiFiScanReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);

		wifiManager.startScan();

		if (isToast) {
			Toast.makeText(mContext, R.string.wifi_reload_done, Toast.LENGTH_SHORT).show();
		}
	}

	private void reload(boolean isToast) {
		unregisterReceiver();
		init(isToast);
	}

	private void unregisterReceiver() {
		try {
			if (wiFiScanReceiver != null) {
				mContext.unregisterReceiver(wiFiScanReceiver);
			}
			if (wifiStateChangedReceiver != null) {
				mContext.unregisterReceiver(wifiStateChangedReceiver);
			}
		} catch (IllegalArgumentException ignored) {
		}
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

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}

	@SuppressLint("HardwareIds")
	private String getData(int j) {
		try {
			StringBuilder text = new StringBuilder();
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
					return getString(R.string.loading);
				case 2:
					try {
						// List saved networks
						List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
						Collections.sort(configs, new Comparator<WifiConfiguration>() {
							@Override
							public int compare(WifiConfiguration lhs, WifiConfiguration rhs) {
								return lhs.SSID.compareToIgnoreCase(rhs.SSID);
							}
						});

						for (WifiConfiguration config : configs) {
							text.append(config.SSID.replaceAll("\"", "")).append("\n");
						}
						text = new StringBuilder(text.length() > 2 ? text.substring(0, text.length() - 2) : text.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return text.toString().trim();
				default:
					return "N/A";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "N/A";
		}
	}

	private void updateScannedList(List<ScanResult> results) {
		StringBuilder text = new StringBuilder();
		Collections.sort(results, new Comparator<ScanResult>() {
			@Override
			public int compare(ScanResult lhs, ScanResult rhs) {
				return lhs.SSID.compareToIgnoreCase(rhs.SSID);
			}
		});

		for (ScanResult result : results) {
			text.append(getScanResultText(result));
		}
		text = new StringBuilder(text.length() > 2 ? text.substring(0, text.length() - 2) : text.toString());

		list.get(1).setContentText(text.toString());
		adapter.notifyDataSetChanged();
	}

	private String getScanResultText(ScanResult result) {
		String text = "";
		text += (result.SSID == null || result.SSID.equals("") ? "__Hidden SSID__" : result.SSID) + "\n";

		String channelWidth = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			switch (result.channelWidth) {
				case ScanResult.CHANNEL_WIDTH_20MHZ:
					channelWidth += "20MHZ";
					break;
				case ScanResult.CHANNEL_WIDTH_40MHZ:
					channelWidth += "40MHZ";
					break;
				case ScanResult.CHANNEL_WIDTH_80MHZ:
					channelWidth += "80MHZ";
					break;
				case ScanResult.CHANNEL_WIDTH_160MHZ:
					channelWidth += "160MHZ";
					break;
				case ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ:
					channelWidth += "80MHZ+";
					break;
			}
		}
		String frequency = result.frequency + "MHZ";
		String level = result.level + "dBm";

		text += (frequency + " " + level + " " + channelWidth).trim() + "\n\n";
		return text;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (hasAllPermissionsGranted(grantResults)) {
				reload(false);
			} else {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
