package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.view.LevelView;

/**
 * Created by himphen on 21/5/16.
 */
public class ToolBubbleLevelFragment extends BaseFragment implements SensorEventListener {

	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
	private Sensor secondSensor = null;

	private float[] accValues = new float[3];
	private float[] magValues = new float[3];
	private float r[] = new float[9];
	private float values[] = new float[3];


	@BindView(R.id.levelView)
	LevelView levelView;
	@BindView(R.id.verticalTv)
	TextView verticalTv;
	@BindView(R.id.horizontalTv)
	TextView horizontalTv;

	public static ToolBubbleLevelFragment newInstance() {
		ToolBubbleLevelFragment fragment = new ToolBubbleLevelFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tool_bubble_level, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mSensor != null && secondSensor != null) {
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
			mSensorManager.registerListener(this, secondSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	private void init() {
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		try {
			if (mSensorManager == null) {
				throw new Exception();
			}

			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			secondSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

			if (mSensor == null || secondSensor == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			C.errorNoFeatureDialog(mContext);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
				accValues = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				magValues = event.values.clone();
				break;

		}

		SensorManager.getRotationMatrix(r, null, accValues, magValues);
		SensorManager.getOrientation(r, values);

		float pitchAngle = values[1];
		float rollAngle = -values[2];

		onAngleChanged(rollAngle, pitchAngle);
	}

	/**
	 * @param rollAngle  float
	 * @param pitchAngle float
	 */
	private void onAngleChanged(float rollAngle, float pitchAngle) {
		levelView.setAngle(rollAngle, pitchAngle);
		horizontalTv.setText(String.format("%s°", String.valueOf((int) Math.toDegrees(rollAngle))));
		verticalTv.setText(String.format("%s°", String.valueOf((int) Math.toDegrees(pitchAngle))));
	}
}
