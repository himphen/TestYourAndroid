package hibernate.v2.testyourandroid.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.helper.SensorHelper;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
@SuppressLint("DefaultLocale")
public class SensorFragment extends BaseFragment {

	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
	private Sensor secondSensor = null;
	private SensorEventListener sensorEventListener = null;

	public static final String ARG_SENSOR_TYPE = "sensorType";

	private int sensorType = 0;

	private String reading = "";

	private float initReading = 0.f;
	private GraphViewSeries series = null;
	private GraphViewSeries series2 = null;
	private GraphViewSeries series3 = null;
	private double lastXValue = 0;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;
	@BindView(R.id.graph2)
	LinearLayout layout;

	private InfoItemAdapter adapter;
	private List<InfoItem> list = new ArrayList<>();

	public static SensorFragment newInstance(int sensorType) {
		SensorFragment fragment = new SensorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SENSOR_TYPE, sensorType);
		fragment.setArguments(args);
		return fragment;
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
		View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
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
		super.onPause();
		if (mSensor != null && sensorEventListener != null) {
			mSensorManager.unregisterListener(sensorEventListener);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mSensor != null && sensorEventListener != null) {

			mSensorManager.registerListener(sensorEventListener, mSensor,
					SensorManager.SENSOR_DELAY_UI);

			if (secondSensor != null) {
				mSensorManager.registerListener(sensorEventListener, secondSensor,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	private void init() {
		list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.test_sensor_string_array);

		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		try {
			if (mSensorManager == null) {
				throw new Exception();
			}

			mSensor = mSensorManager.getDefaultSensor(sensorType);

			if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
				secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}

			if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
				secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			}

			if (mSensor == null && secondSensor == null) {
				C.errorNoFeatureDialog(mContext);
				return;
			}
		} catch (Exception e) {
			C.errorNoFeatureDialog(mContext);
			return;
		}

		boolean isGraph2 = false;
		boolean isGraph3 = false;
		GraphView graphView = new LineGraphView(mContext, "");
		graphView.setShowHorizontalLabels(false);

		switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
			case Sensor.TYPE_GRAVITY:
				sensorEventListener = accelerometerListener;
				isGraph2 = isGraph3 = true;
				break;
			case Sensor.TYPE_LIGHT:
				sensorEventListener = lightListener;
				break;
			case Sensor.TYPE_PRESSURE:
				sensorEventListener = pressureListener;
				graphView.setManualYAxisBounds(mSensor.getMaximumRange(), 0);
				break;
			case Sensor.TYPE_PROXIMITY:
				sensorEventListener = proximityListener;
				graphView.setManualYAxisBounds(mSensor.getMaximumRange(), 0);
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				sensorEventListener = compassListener;
				graphView.setManualYAxisBounds(360, 0);
				break;
			case Sensor.TYPE_STEP_COUNTER:
				sensorEventListener = stepListener;
				break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				sensorEventListener = temperatureListener;
				break;
			case Sensor.TYPE_RELATIVE_HUMIDITY:
				sensorEventListener = humidityListener;
				break;
		}

		GraphView.GraphViewData[] data = new GraphView.GraphViewData[]{};
		series = new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(
				ContextCompat.getColor(mContext, R.color.blue500), 3), data);
		graphView.addSeries(series);

		if (isGraph2) {
			GraphView.GraphViewData[] data1 = new GraphView.GraphViewData[]{};
			series2 = new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(
					ContextCompat.getColor(mContext, R.color.pink500), 3), data1);
			graphView.addSeries(series2);
		}

		if (isGraph3) {
			GraphView.GraphViewData[] data2 = new GraphView.GraphViewData[]{};
			series3 = new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(
					ContextCompat.getColor(mContext, R.color.green500), 3), data2);
			graphView.addSeries(series3);
		}

		InfoItem infoItem;
		for (int i = 0; i < stringArray.length; i++) {
			try {
				switch (sensorType) {
					case Sensor.TYPE_ACCELEROMETER:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getAccelerometerSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_GRAVITY:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getGravitySensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_LIGHT:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getLightSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_PRESSURE:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getPressureSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_PROXIMITY:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getProximitySensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_MAGNETIC_FIELD:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getMagneticSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_STEP_COUNTER:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getStepCounterSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_AMBIENT_TEMPERATURE:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getTemperatureCounterSensorData(i, stringArray.length, reading, mSensor));
						break;
					case Sensor.TYPE_RELATIVE_HUMIDITY:
						infoItem = new InfoItem(stringArray[i], SensorHelper.getHumiditySensorData(i, stringArray.length, reading, mSensor));
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

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);

		graphView.setGraphViewStyle(new GraphViewStyle(Color.GRAY, Color.GRAY,
				Color.TRANSPARENT));
		graphView.setViewPort(0, 36);
		graphView.setScalable(true);
		graphView.setScrollable(true);
		graphView.setDisableTouch(true);
		layout.addView(graphView);
	}

	private SensorEventListener accelerometerListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = "X: " + String.format("%1.4f", event.values[0]) + " m/s²\nY: "
					+ String.format("%1.4f", event.values[1]) + " m/s²\nZ: "
					+ String.format("%1.4f", event.values[2]) + " m/s²";

			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, event.values[0]),
					true, 100);
			series2.appendData(new GraphView.GraphViewData(lastXValue, event.values[1]),
					true, 100);
			series3.appendData(new GraphView.GraphViewData(lastXValue, event.values[2]),
					true, 100);

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

			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, event.values[0]),
					true, 100);

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
			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, event.values[0]),
					true, 100);
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener proximityListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			reading = String.format("%1.2f", event.values[0]) + " cm";
			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, event.values[0]),
					true, 100);
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener stepListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (initReading == 0) {
				initReading = event.values[0];
			}
			int value = (int) (event.values[0] - initReading);
			reading = value + " Steps";
			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, value),
					true, 100);
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener temperatureListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			double valueC = event.values[0];
			double valueF = valueC * 1.8 + 32;
			reading = String.format("%1.2f", valueC) + " °C\n" + String.format("%1.2f", valueF) + " °F";
			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, event.values[0]),
					true, 100);
			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private SensorEventListener compassListener = new SensorEventListener() {

		private float[] accelerometerValues = new float[3];
		private float[] magneticFieldValues = new float[3];

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				magneticFieldValues = event.values;
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				accelerometerValues = event.values;

			float[] values = new float[3];
			float[] R = new float[9];
			SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
			SensorManager.getOrientation(R, values);

			values[0] = (int) Math.toDegrees(values[0]);

			if (values[0] < 0) {
				values[0] = values[0] + 359;
			}

			lastXValue += 1;
			series.appendData(new GraphView.GraphViewData(lastXValue, values[0]),
					true, 100);

			if (values[0] >= 315 || values[0] < 45) {
				reading = "N " + values[0] + "°";
			} else if (values[0] >= 45 && values[0] < 135) {
				reading = "E " + values[0] + "°";
			} else if (values[0] >= 135 && values[0] < 225) {
				reading = "S " + values[0] + "°";
			} else if (values[0] >= 225 && values[0] < 315) {
				reading = "W " + values[0] + "°";
			}

			list.get(0).setContentText(reading);
			adapter.notifyDataSetChanged();
		}
	};

	private float mLastKnownRelativeHumidity = 0;
	private float mLastKnownTemperature = 0;
	private float mLastKnownAbsoluteHumidity = 0;
	private float mLastKnownDewPoint = 0;

	private SensorEventListener humidityListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
				mLastKnownRelativeHumidity = event.values[0];
			} else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
				if (mLastKnownRelativeHumidity != 0) {
					mLastKnownTemperature = event.values[0];
					mLastKnownAbsoluteHumidity = calculateAbsoluteHumidity(mLastKnownTemperature, mLastKnownRelativeHumidity);
					mLastKnownDewPoint = calculateDewPoint(mLastKnownTemperature, mLastKnownRelativeHumidity);
				}
			}

			reading = getString(R.string.ui_relative_humidity) + mLastKnownRelativeHumidity + "%";
			reading += getString(R.string.ui_absolute_humidity) + mLastKnownTemperature + "°C/" + mLastKnownAbsoluteHumidity + "%";
			reading += getString(R.string.ui_dew_point) + mLastKnownTemperature + "°C/" + mLastKnownDewPoint;

		}
	};

	/* Meaning of the constants
     Dv: Absolute humidity in grams/meter3
     m: Mass constant
     Tn: Temperature constant
     Ta: Temperature constant
     Rh: Actual relative humidity in percent (%) from phone’s sensor
     Tc: Current temperature in degrees C from phone’ sensor
     A: Pressure constant in hP
     K: Temperature constant for converting to kelvin
     */
	@SuppressWarnings({"UnnecessaryLocalVariable", "UnusedAssignment"})
	public float calculateAbsoluteHumidity(float temperature, float relativeHumidity) {
		float Dv = 0;
		float m = 17.62f;
		float Tn = 243.12f;
		float Ta = 216.7f;
		float Rh = relativeHumidity;
		float Tc = temperature;
		float A = 6.112f;
		float K = 273.15f;

		Dv = (float) (Ta * (Rh / 100) * A * Math.exp(m * Tc / (Tn + Tc)) / (K + Tc));

		return Dv;
	}

	/* Meaning of the constants
    Td: Dew point temperature in degrees Celsius
    m: Mass constant
    Tn: Temperature constant
    Rh: Actual relative humidity in percent (%) from phone’s sensor
    Tc: Current temperature in degrees C from phone’ sensor
    */
	@SuppressWarnings({"UnnecessaryLocalVariable", "UnusedAssignment"})
	public float calculateDewPoint(float temperature, float relativeHumidity) {
		float Td = 0;
		float m = 17.62f;
		float Tn = 243.12f;
		float Rh = relativeHumidity;
		float Tc = temperature;

		Td = (float) (Tn * ((Math.log(Rh / 100) + m * Tc / (Tn + Tc)) / (m - (Math.log(Rh / 100) + m * Tc / (Tn + Tc)))));

		return Td;
	}
}
