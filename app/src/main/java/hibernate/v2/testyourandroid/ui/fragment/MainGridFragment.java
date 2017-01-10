package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.GridItem;
import hibernate.v2.testyourandroid.ui.activity.AppTypeChooseActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoAndroidVersionActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoBatteryActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoCPUActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoCameraActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoGSMActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoHardwareActivity;
import hibernate.v2.testyourandroid.ui.activity.MainActivity;
import hibernate.v2.testyourandroid.ui.activity.TestCameraActivity;
import hibernate.v2.testyourandroid.ui.activity.TestColorActivity;
import hibernate.v2.testyourandroid.ui.activity.TestDrawActivity;
import hibernate.v2.testyourandroid.ui.activity.TestEasterEggActivity;
import hibernate.v2.testyourandroid.ui.activity.TestFingerprintActivity;
import hibernate.v2.testyourandroid.ui.activity.TestFlashActivity;
import hibernate.v2.testyourandroid.ui.activity.TestLocationActivity;
import hibernate.v2.testyourandroid.ui.activity.TestMicActivity;
import hibernate.v2.testyourandroid.ui.activity.TestMonitorActivity;
import hibernate.v2.testyourandroid.ui.activity.TestMultiTouchActivity;
import hibernate.v2.testyourandroid.ui.activity.TestNFCActivity;
import hibernate.v2.testyourandroid.ui.activity.TestRingActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorAccelerometerActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorCompassActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorGravityActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorLightActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorPressureActivity;
import hibernate.v2.testyourandroid.ui.activity.TestSensorProximityActivity;
import hibernate.v2.testyourandroid.ui.activity.TestWifiActivity;
import hibernate.v2.testyourandroid.ui.adapter.GridItemAdapter;

public class MainGridFragment extends BaseFragment {

	public static final String ARG_GRID_TYPE = "ARG_GRID_TYPE";
	public static final int ARG_GRID_TYPE_TEST = 1;
	public static final int ARG_GRID_TYPE_INFO = 2;

	private Integer[] imageInfoArray = {
			R.drawable.ic_cpu, R.drawable.ic_device, R.drawable.ic_android,
			R.drawable.ic_battery, R.drawable.ic_test_camera,
			R.drawable.ic_network, R.drawable.ic_apps};

	private Class[] classInfoArray = {
			InfoCPUActivity.class, InfoHardwareActivity.class, InfoAndroidVersionActivity.class,
			InfoBatteryActivity.class, InfoCameraActivity.class, InfoGSMActivity.class, AppTypeChooseActivity.class
	};

	private Integer[] imageTestArray = {
			R.drawable.ic_test_screen, R.drawable.ic_test_draw, R.drawable.ic_test_touch,
			R.drawable.ic_test_camera, R.drawable.ic_test_fingerprint,
			R.drawable.ic_test_flashlight, R.drawable.ic_test_ring, R.drawable.ic_test_mic,
			R.drawable.ic_test_nfc, R.drawable.ic_test_wifi, R.drawable.ic_test_gps,
			R.drawable.ic_test_compass, R.drawable.ic_test_system,
			R.drawable.ic_test_chip, R.drawable.ic_test_chip,
			R.drawable.ic_test_chip, R.drawable.ic_test_chip,
			R.drawable.ic_test_chip, R.drawable.ic_test_easteregg};

	private Class[] classTestArray = {
			TestColorActivity.class, TestDrawActivity.class, TestMultiTouchActivity.class,
			TestCameraActivity.class, TestFingerprintActivity.class, TestFlashActivity.class,
			TestRingActivity.class, TestMicActivity.class,
			TestNFCActivity.class, TestWifiActivity.class, TestLocationActivity.class,
			TestSensorCompassActivity.class, TestMonitorActivity.class,
			TestSensorLightActivity.class, TestSensorAccelerometerActivity.class,
			TestSensorProximityActivity.class, TestSensorPressureActivity.class,
			TestSensorGravityActivity.class, TestEasterEggActivity.class
	};

	@BindView(R.id.gridRv)
	RecyclerView recyclerView;

	private int gridType = ARG_GRID_TYPE_TEST;

	public MainGridFragment() {
	}

	public static MainGridFragment newInstance(int sensorType) {
		MainGridFragment fragment = new MainGridFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_GRID_TYPE, sensorType);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			gridType = getArguments().getInt(ARG_GRID_TYPE, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_gridview, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Integer[] imageArray;
		Class[] classArray;
		String[] stringArray;
		switch (gridType) {
			case 2:
				imageArray = imageInfoArray;
				classArray = classInfoArray;
				stringArray = getResources().getStringArray(R.array.main_info_string_array);
				break;
			default:
				imageArray = imageTestArray;
				classArray = classTestArray;
				stringArray = getResources().getStringArray(R.array.main_test_string_array);
				break;
		}

		ArrayList<Integer> imageList = new ArrayList<>(Arrays.asList(imageArray));
		ArrayList<Class> classList = new ArrayList<>(Arrays.asList(classArray));

		List<GridItem> list = new ArrayList<>();
		for (int i = 0; i < imageList.size(); i++) {
			list.add(new GridItem(stringArray[i], imageList.get(i), classList.get(i)));
		}

		// Manually add rate icon
		list.add(new GridItem(getString(R.string.title_activity_rate_us), R.drawable.ic_rating, "rate"));

		// Manually add donate icon if not donated
		if (!PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(C.PREF_IAP, false)) {
			list.add(new GridItem(getString(R.string.title_activity_test_ad_remover), R.drawable.ic_test_ad_remover, "donate"));
		}

		int spanCount = 1;
		int columnCount = 3;
		GridLayoutManager man = new GridLayoutManager(getActivity(), spanCount);
		man.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return 1;
			}
		});
		if (C.isTablet(mContext)
				&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			columnCount = 4;
		}
		GridItemAdapter.ItemClickListener mListener = new GridItemAdapter.ItemClickListener() {
			@Override
			public void onItemDetailClick(GridItem gridItem) {
				if (gridItem.getIntentClass() != null) {
					Intent intent = new Intent().setClass(mContext, gridItem.getIntentClass());
					startActivity(intent);
				} else {
					switch (gridItem.getActionType()) {
						case "donate":
							((MainActivity) getActivity()).checkPayment();
							break;
						case "rate":
							Uri uri = Uri.parse("market://details?id=hibernate.v2.testyourandroid");
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							break;
					}
				}
			}
		};
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new GridLayoutManager(mContext, columnCount));
		recyclerView.setAdapter(new GridItemAdapter(list, mListener));
	}
}