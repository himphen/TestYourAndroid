package hibernate.v2.testyourandroid.ui.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

public class MonitorMemoryFragment extends BaseFragment {

	private GraphViewSeries series;
	private double lastXValue = 0;
	private long availableValue = 0;
	private long usedValue = 0;
	private long totalValue = 0;

	private Handler mHandler = new Handler();

	private Runnable timer = new Runnable() {
		@Override
		public void run() {
			getRamMemory();
			lastXValue += 1;
			try {
				double v = Double
						.parseDouble(C.formatBitSize(usedValue, false));
				usedText.setText(C.formatBitSize(usedValue, true));
				avaText.setText(C.formatBitSize(availableValue, true));
				Log.d(C.TAG, "appendData " + v);
				series.appendData(
						new GraphViewData(lastXValue, v), true, 100);
			} catch (Exception e) {
				avaText.setText(R.string.notsupport);
				usedText.setText(R.string.notsupport);
			}
			mHandler.postDelayed(timer, 1000);
		}
	};

	@BindView(R.id.graph2)
	LinearLayout layout;
	@BindView(R.id.totalText)
	TextView totalText;
	@BindView(R.id.avaText)
	TextView avaText;
	@BindView(R.id.usedText)
	TextView usedText;
	private boolean isSupported = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor_memory, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getRamMemory();
		avaText.setText(C.formatBitSize(availableValue, true));
		totalText.setText(C.formatBitSize(totalValue, true));

		GraphView graphView = new LineGraphView(mContext, "");
		graphView.setShowHorizontalLabels(false);
		GraphViewData[] data = new GraphViewData[]{new GraphViewData(0, 0)};
		series = new GraphViewSeries("", new GraphViewSeriesStyle(
				Color.parseColor("#FF8800"), 3), data);
		graphView.setGraphViewStyle(new GraphViewStyle(Color.GRAY, Color.GRAY,
				Color.LTGRAY));
		graphView.addSeries(series);
		try {
			isSupported = true;
			graphView.setManualYAxisBounds(
					Double.parseDouble(C.formatBitSize(totalValue, false)), 0);
			graphView.setViewPort(0, 36);
			graphView.setScalable(true);
			graphView.setScrollable(true);
			graphView.setDisableTouch(true);
			layout.addView(graphView);
		} catch (NumberFormatException e) {
			usedText.setText(R.string.ui_na);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isSupported)
			mHandler.postDelayed(timer, 1000);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isSupported)
			mHandler.removeCallbacks(timer);
	}

	private void getRamMemory() {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		long totalMem;
		totalMem = memoryInfo.totalMem;

		totalValue = totalMem;
		usedValue = totalMem - memoryInfo.availMem;
		availableValue = memoryInfo.availMem;
	}
}