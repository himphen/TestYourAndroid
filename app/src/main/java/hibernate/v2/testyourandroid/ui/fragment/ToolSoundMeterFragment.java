package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class ToolSoundMeterFragment extends BaseFragment {

	protected final String[] PERMISSION_NAME = {Manifest.permission.RECORD_AUDIO};

	@BindView(R.id.meterCurrentTv)
	TextView meterCurrentTv;
	@BindView(R.id.meterAvgTv)
	TextView meterAvgTv;
	@BindView(R.id.meterMaxTv)
	TextView meterMaxTv;
	@BindView(R.id.meterMinTv)
	TextView meterMinTv;
	@BindView(R.id.graph2)
	LinearLayout layout;


	static final int SAMPLE_RATE_IN_HZ = 44100;

	int BUFFER_SIZE;
	short[] buffer;

	private GraphViewSeries series = null;
	private double lastXValue = 0;

	private Integer mindB = null;
	private Integer maxdB = null;
	private ArrayList<Integer> avgdB = new ArrayList<>();

	private boolean mIsRecording = false;
	private AudioRecord mAudioRecord;

	public static ToolSoundMeterFragment newInstance() {
		ToolSoundMeterFragment fragment = new ToolSoundMeterFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tool_sound_meter, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (!isPermissionsGranted(PERMISSION_NAME)) {
			requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onPause() {
		stopRecording();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (isPermissionsGranted(PERMISSION_NAME)) {
			startRecording();
		}
	}

	private void startRecording() {
		BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
				AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);

		if (BUFFER_SIZE < 0) {
			C.errorNoFeatureDialog(mContext);
			return;
		}

		buffer = new short[BUFFER_SIZE];
		try {
			LineGraphView graphView = new LineGraphView(mContext, "");
			graphView.setShowHorizontalLabels(false);
			series = new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(
					ContextCompat.getColor(mContext, R.color.green500), 3), new GraphView.GraphViewData[]{});
			graphView.addSeries(series);
			graphView.setManualYAxisBounds(120, 0);
			graphView.setGraphViewStyle(new GraphViewStyle(Color.GRAY, Color.GRAY,
					Color.TRANSPARENT));
			graphView.setViewPort(0, 36);
			graphView.setScalable(true);
			graphView.setScrollable(true);
			graphView.setDisableTouch(true);
			layout.addView(graphView);

			mAudioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					SAMPLE_RATE_IN_HZ,
					AudioFormat.CHANNEL_IN_DEFAULT,
					AudioFormat.ENCODING_PCM_16BIT,
					AudioRecord.getMinBufferSize(
							SAMPLE_RATE_IN_HZ,
							AudioFormat.CHANNEL_IN_DEFAULT,
							AudioFormat.ENCODING_PCM_16BIT
					)
			);
			mAudioRecord.startRecording();

			mIsRecording = true;

			meterCurrentTv.post(new Runnable() {
				public void run() {
					if (mIsRecording) {
						int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
						long v = 0;

						for (short aBuffer : buffer) {
							v += aBuffer * aBuffer;
						}

						double mean = v / (double) r;
						Integer db = Math.round(10 * (float) Math.log10(mean));

						if (db < 0) {
							db = 0;
						}

						if (maxdB == null || db > maxdB) {
							maxdB = db;
							meterMaxTv.setText(String.valueOf(maxdB));
						}

						if (mindB == null || db < mindB) {
							mindB = db;
							meterMinTv.setText(String.valueOf(mindB));
						}

						avgdB.add(db);

						meterAvgTv.setText(String.valueOf(Math.round(C.calculateAverage(avgdB))));
						meterCurrentTv.setText(String.valueOf(db));

						lastXValue += 0.5;
						series.appendData(new GraphView.GraphViewData(lastXValue, db),
								true, 100);

						if (db > 100) {
							series.getStyle().color = ContextCompat.getColor(mContext, R.color.pink500);
						} else if (db > 80) {
							series.getStyle().color = ContextCompat.getColor(mContext, R.color.gold);
						} else {
							series.getStyle().color = ContextCompat.getColor(mContext, R.color.green500);
						}

						meterCurrentTv.postDelayed(this, 500);
					}
				}
			});
		} catch (Exception e) {
			C.logException(e);
			C.errorNoFeatureDialog(mContext);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void stopRecording() {
		if (mAudioRecord != null) {
			if (mIsRecording) {
				mIsRecording = false;
				try {
					mAudioRecord.stop();
				} catch (Exception ignored) {
				}
			}

			mAudioRecord.release();
			mAudioRecord = null;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (!hasAllPermissionsGranted(grantResults)) {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
