package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.ExtendedBluetoothDevice;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoBluetoothFragment extends BaseFragment {

	protected final String PERMISSION_NAME = Manifest.permission.ACCESS_COARSE_LOCATION;

	private List<ExtendedBluetoothDevice> scannedList = new ArrayList<>();
	private List<InfoItem> list = new ArrayList<>();
	private InfoItemAdapter adapter;
	private boolean isFirstLoading = true;

	private BluetoothAdapter bluetoothAdapter;
	private BroadcastReceiver bluetoothChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

				ExtendedBluetoothDevice edevice = new ExtendedBluetoothDevice();
				edevice.setName(device.getName() == null ? device.getAddress() : device.getName());
				edevice.setRiss(rssi);
				updateScannedList(edevice);
			}
		}
	};

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {

			if (isFirstLoading) {
				reload(false);
				isFirstLoading = false;
			} else {
				reload(true);
			}
		} else {
			requestPermissions(new String[]{PERMISSION_NAME}, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_reload:
				reload(true);
				break;
			case R.id.action_settings:
				try {
					startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Settings.ACTION_SETTINGS));
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(boolean isToast) {
		list = new ArrayList<>();
		scannedList.clear();
		String[] stringArray = getResources().getStringArray(R.array.test_bluetooth_string_array);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Register Broadcast Receiver
		if (bluetoothAdapter != null) {
			if (!bluetoothAdapter.isEnabled()) {
				openBluetoothDialog();
				return;
			}
		} else {
			C.errorNoFeatureDialog(mContext);
			return;
		}

		mContext.registerReceiver(bluetoothChangedReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		bluetoothAdapter.startDiscovery();

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);

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
			if (bluetoothChangedReceiver != null) {
				mContext.unregisterReceiver(bluetoothChangedReceiver);
			}
		} catch (IllegalArgumentException ignored) {
		}
	}

	private void openBluetoothDialog() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_caution)
				.content(R.string.bluetooth_enable_message)
				.cancelable(false)
				.negativeText(R.string.ui_cancel)
				.positiveText(R.string.bluetooth_enable_posbtn)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						try {
							startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
						} catch (ActivityNotFoundException e) {
							startActivity(new Intent(Settings.ACTION_SETTINGS));
						}
					}
				});
		dialog.show();
	}

	private String getData(int j) {
		try {
			StringBuilder text = new StringBuilder();
			switch (j) {
				case 0:
					return getString(R.string.loading);
				case 1:
					try {
						// List paired devices
						Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
						for (BluetoothDevice result : pairedDevices) {
							text.append(result.getName()).append("\n");
						}
						text = new StringBuilder(text.length() > 1 ? text.substring(0, text.length() - 1) : text.toString());
					} catch (Exception ignored) {
					}
					return text.toString();
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}

	private void updateScannedList(ExtendedBluetoothDevice device) {
		scannedList.add(device);
		Collections.sort(scannedList, new Comparator<ExtendedBluetoothDevice>() {
			@Override
			public int compare(ExtendedBluetoothDevice lhs, ExtendedBluetoothDevice rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});

		StringBuilder text = new StringBuilder();
		for (ExtendedBluetoothDevice item : scannedList) {
			text.append(getScanResultText(item));
		}
		text = new StringBuilder(text.length() > 2 ? text.substring(0, text.length() - 2) : text.toString());

		list.get(0).setContentText(text.toString());
		adapter.notifyDataSetChanged();
	}

	private String getScanResultText(ExtendedBluetoothDevice result) {
		String text = "";
		text += result.getName();
		text += "\n";

		String riss = result.getRiss();
		text += riss.trim();
		text += "\n\n";
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
