package hibernate.v2.testyourandroid.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
public class InfoBatteryFragment extends BaseFragment {

	private List<InfoItem> list = new ArrayList<>();

	private InfoItemAdapter adapter;

	private int health = 0;
	private int level = 0;
	private int charge = 0;
	private int scale = 0;
	private String technology = "";
	private int voltage = 0;
	private double celsiusTemperature = 0;
	private double fahrenheitTemperature = 0;

	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			technology = intent.getExtras().getString(
					BatteryManager.EXTRA_TECHNOLOGY);
			int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
			voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
			level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			charge = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

			celsiusTemperature = (double) temperature / 10;
			fahrenheitTemperature = C.round(32 + (celsiusTemperature * 9 / 5), 2);

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setContentText(getData(i));
				}
			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	};
	private String[] chargeString;
	private String[] healthString;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	public InfoBatteryFragment() {
		// Required empty public constructor
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
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		init();
	}

	@Override
	public void onPause() {
		mContext.unregisterReceiver(batteryReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mContext.registerReceiver(batteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	private void init() {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_battery_string_array);
		chargeString = getResources().getStringArray(R.array.info_battery_charge_string_array);
		healthString = getResources().getStringArray(R.array.info_battery_health_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);

		recyclerView.setAdapter(adapter);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return level + " %";
				case 1:
					return scale + " %";
				case 2:
					return healthString[health];
				case 3:
					return chargeString[charge];
				case 4:
					return technology;
				case 5:
					return celsiusTemperature + " C / " + fahrenheitTemperature + " F";
				case 6:
					return voltage + " mV";
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
