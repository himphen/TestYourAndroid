package hibernate.v2.testyourandroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoHeader;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adaptor.InfoItemAdaptor;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoHardwareFragment extends BaseFragment {

	private int level = 0;
	private int charge = 0;

	private int health = 0;
	private String[] arrayCharge;
	private String[] arrayHealth;
	private TelephonyManager telephonyManager;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private InfoItemAdaptor adapter;
	private ArrayList<InfoItem> list;

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			if (charge > 4)
				charge = 3;
			health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

			if (list != null) {
				list.get(2).setContentText(level + " %");
				list.get(3).setContentText(arrayHealth[health]);
				list.get(4).setContentText(arrayCharge[charge]);
			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	};

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
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		init();
	}

	@Override
	public void onPause() {
		mContext.unregisterReceiver(mBatInfoReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mContext.registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	private void init() {
		telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

		arrayCharge = getResources().getStringArray(R.array.info_battery_charge_string_array);
		arrayHealth = getResources().getStringArray(R.array.info_battery_health_string_array);

		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_hardware_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdaptor(list);
		adapter.setHeader(new InfoHeader(mContext.getTitle().toString()));
		recyclerView.setAdapter(adapter);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return Build.BRAND;
				case 1:
					return Build.DEVICE;
				case 2:
					return level + "%";
				case 3:
					return arrayHealth[health];
				case 4:
					return arrayCharge[charge];
				case 5:
					return Build.VERSION.RELEASE;
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
