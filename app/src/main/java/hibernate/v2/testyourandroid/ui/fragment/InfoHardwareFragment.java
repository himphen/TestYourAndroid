package hibernate.v2.testyourandroid.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoHardwareFragment extends BaseFragment {

	private List<InfoItem> list = new ArrayList<>();
	private InfoItemAdapter adapter;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	public InfoHardwareFragment() {
		// Required empty public constructor
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

	private void init() {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_hardware_string_array);

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
					return Build.BRAND;
				case 1:
					return Build.DEVICE;
				case 2:
					return Build.MODEL;
				case 3:
					return Build.PRODUCT;
				case 4:
					return Build.DISPLAY;
				case 5:
					return Build.FINGERPRINT;
				case 6:
					return Build.BOARD;
				case 7:
					return Build.HARDWARE;
				case 8:
					return Build.MANUFACTURER;
				case 9:
					return Build.SERIAL;
				case 10:
					return Build.USER;
				case 11:
					return Build.HOST;
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
