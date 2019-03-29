package hibernate.v2.testyourandroid.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoHeader;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adaptor.InfoItemAdaptor;
import hibernate.v2.testyourandroid.helper.SensorHelper;

/**
 * Created by himphen on 21/5/16.
 */
public class TestSensorFragment extends BaseFragment {

	private SensorManager mgr;
	private Sensor sensor;
	private SensorEventListener sensorEventListener = null;

	public static final String ARG_SENSOR_TYPE = "sensorType";

	private int sensorType = 0;

	private String reading = "";
	private InfoItemAdaptor adapter;
	private List<InfoItem> list = new ArrayList<>();

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	public static TestSensorFragment newInstance(int sensorType) {
		TestSensorFragment fragment = new TestSensorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SENSOR_TYPE, sensorType);
		fragment.setArguments(args);
		return fragment;
	}

	public TestSensorFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			sensorType = getArguments().getInt(ARG_SENSOR_TYPE, 0);
		}
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
						RecyclerView.VERTICAL, false)
		);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (sensor != null && sensorEventListener != null) {
			mgr.unregisterListener(sensorEventListener);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sensor != null && sensorEventListener != null) {
			mgr.registerListener(sensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private void init() {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.test_sensor_string_array);

		mgr = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		try {
			sensor = mgr.getDefaultSensor(sensorType);
			if (sensor == null) {
				Toast.makeText(mContext, R.string.dialog_feature_na_message, Toast.LENGTH_LONG).show();
				mContext.finish();
				return;
			}
		} catch (Exception e) {
			Toast.makeText(mContext, R.string.dialog_feature_na_message, Toast.LENGTH_LONG).show();
			mContext.finish();
			return;
		}

		switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
				sensorEventListener = accelerometerListener;
				break;
			case Sensor.TYPE_GRAVITY:
				sensorEventListener = accelerometerListener;
				break;
			case Sensor.TYPE_LIGHT:
				sensorEventListener = lightListener;
				break;
			case Sensor.TYPE_PRESSURE:
				sensorEventListener = pressureListener;
				break;
			case Sensor.TYPE_PROXIMITY:
				sensorEventListener = proximityListener;
				break;
			default:
		}
		InfoItem infoItem;
		for (int i = 0; i < stringArray.length; i++) {
			try {
				switch (sensorType) {
					case Sensor.TYPE_ACCELEROMETER:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getAccelerometerSensorData(i, stringArray.length, reading, sensor));
						break;
					case Sensor.TYPE_GRAVITY:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getGravitySensorData(i, stringArray.length, reading, sensor));
						break;
					case Sensor.TYPE_LIGHT:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getLightSensorData(i, stringArray.length, reading, sensor));
						break;
					case Sensor.TYPE_PRESSURE:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getPressureSensorData(i, stringArray.length, reading, sensor));
						break;
					case Sensor.TYPE_PROXIMITY:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getProximitySensorData(i, stringArray.length, reading, sensor));
						break;
					default:
						infoItem = new InfoItem(stringArray[i], getString(R.string.ui_not_support));
				}
			} catch (Exception e) {
				e.printStackTrace();
				infoItem = new InfoItem(stringArray[i], getString(R.string.ui_not_support));
			}

			list.add(infoItem);
		}

		adapter = new InfoItemAdaptor(list);
		adapter.setHeader(new InfoHeader(mContext.getTitle().toString()));
		recyclerView.setAdapter(adapter);
	}

	private SensorEventListener accelerometerListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = "X: " + String.format("%1.4f", event.values[0]) + " m/s²\nY: "
					+ String.format("%1.4f", event.values[1]) + " m/s²\nZ: "
					+ String.format("%1.4f", event.values[2]) + " m/s²";
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener lightListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = String.valueOf(event.values[0]) + " lux";
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener pressureListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = String.valueOf(event.values[0]) + " hPa";
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener proximityListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = String.format("%1.2f", event.values[0]) + " cm";
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

}
