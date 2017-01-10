package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.MainItem;
import hibernate.v2.testyourandroid.ui.activity.InfoAndroidVersionActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoBatteryActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoCPUActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoHardwareActivity;
import hibernate.v2.testyourandroid.ui.activity.TestColorActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorAccelerometerActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorGravityActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorLightActivity;
import hibernate.v2.testyourandroid.ui.adaptor.MainItemAdaptor;

/**
 * Created by himphen on 21/5/16.
 */
public class MainFragment extends BaseFragment {

	@BindView(R.id.wearableListView)
	WearableListView mWearableListView;

//	private int[] imageArray = {
//			R.drawable.ic_test_screen, R.drawable.ic_test_draw,
//			R.drawable.ic_test_ring, R.drawable.ic_test_wifi,
//			R.drawable.ic_test_gps, R.drawable.ic_test_compass,
//			R.drawable.ic_test_system, R.drawable.ic_test_chip,
//			R.drawable.ic_test_chip, R.drawable.ic_test_chip,
//			R.drawable.ic_test_chip,
//			R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info};
//
//	private Class[] classArray = {
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class,
//			MainActivity.class, MainActivity.class, MainActivity.class, MainActivity.class, MainActivity.class
//	};

	private int[] imageArray = {
			R.drawable.ic_test_screen, R.drawable.ic_test_chip,
			R.drawable.ic_test_chip, R.drawable.ic_test_chip,
			R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info, R.drawable.ic_info
	};

	private Class[] classArray = {
			TestColorActivity.class, TestSensorLightActivity.class,
			TestSensorAccelerometerActivity.class, TestSensorGravityActivity.class,
			InfoCPUActivity.class, InfoHardwareActivity.class,
			InfoAndroidVersionActivity.class, InfoBatteryActivity.class
	};

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String[] stringArray = getResources().getStringArray(R.array.main_test_string_array);

		List<MainItem> items = new ArrayList<>();
		for (int i = 0; i < stringArray.length; i++) {
			MainItem item = new MainItem(stringArray[i], imageArray[i], classArray[i]);
			items.add(item);
		}

		MainItemAdaptor.ItemClickListener mListener = new MainItemAdaptor.ItemClickListener() {
			@Override
			public void onItemDetailClick(MainItem item) {
				startActivity(new Intent(mContext, item.getIntentClass()));
			}
		};
		MainItemAdaptor adapter = new MainItemAdaptor(items, mListener);
		mWearableListView.setAdapter(adapter);

	}

}
