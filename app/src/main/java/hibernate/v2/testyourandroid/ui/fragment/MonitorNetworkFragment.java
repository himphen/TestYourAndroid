package hibernate.v2.testyourandroid.ui.fragment;

import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

public class MonitorNetworkFragment extends BaseFragment {

	private GraphViewSeries series;
	private GraphViewSeries series2;
	private double lastXValue = 0;

	private Handler mHandler = new Handler();
	private long differenceRx = 0;
	private long differenceTx = 0;
	private long lastTotalRx = 0;
	private long lastTotalTx = 0;

	@BindView(R.id.graph2)
	LinearLayout layout;
	@BindView(R.id.upSpeedText)
	TextView upSpeedText;
	@BindView(R.id.downSpeedText)
	TextView downSpeedText;

	private Runnable timer = new Runnable() {
		@Override
		public void run() {
			getNetworkUsage();
			lastXValue += 1;

			try {
				double Tx = Double
						.parseDouble(C.formatSpeedSize(differenceTx, false));
				double Rx = Double
						.parseDouble(C.formatSpeedSize(differenceRx, false));

				upSpeedText.setText(C.formatSpeedSize(differenceTx, true));
				downSpeedText.setText(C.formatSpeedSize(differenceRx, true));
				series.appendData(
						new GraphViewData(lastXValue, Rx),
						true, 100);
				series2.appendData(
						new GraphViewData(lastXValue, Tx),
						true, 100);
			} catch (Exception e) {
				upSpeedText.setText(R.string.notsupport);
				downSpeedText.setText(R.string.notsupport);
			}
			mHandler.postDelayed(timer, 1000);
		}
	};
	private boolean isSupported = false;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor_network, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		GraphView graphView = new LineGraphView(mContext, "");
		graphView.setShowHorizontalLabels(false);
		lastTotalRx = TrafficStats.getTotalRxBytes();
		lastTotalTx = TrafficStats.getTotalTxBytes();

		if (lastTotalRx == TrafficStats.UNSUPPORTED
				|| lastTotalTx == TrafficStats.UNSUPPORTED) {
			upSpeedText.setText(R.string.notsupport);
			downSpeedText.setText(R.string.notsupport);
		} else {
			isSupported = true;
			GraphViewData[] data = new GraphViewData[]{new GraphViewData(0, 0)};
			GraphViewData[] data2 = new GraphViewData[]{new GraphViewData(0, 0)};
			series = new GraphViewSeries("", new GraphViewSeriesStyle(
					Color.parseColor("#AA66CC"), 3), data);
			series2 = new GraphViewSeries("", new GraphViewSeriesStyle(
					Color.parseColor("#99CC00"), 3), data2);
			graphView.addSeries(series);
			graphView.addSeries(series2);
			graphView.setGraphViewStyle(new GraphViewStyle(Color.GRAY,
					Color.GRAY, Color.LTGRAY));
			graphView.setViewPort(0, 36);
			graphView.setScalable(true);
			graphView.setScrollable(true);
			graphView.setDisableTouch(true);
			layout.addView(graphView);
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

	private void getNetworkUsage() {
		differenceRx = TrafficStats.getTotalRxBytes() - lastTotalRx;
		lastTotalRx = TrafficStats.getTotalRxBytes();
		differenceTx = TrafficStats.getTotalTxBytes() - lastTotalTx;
		lastTotalTx = TrafficStats.getTotalTxBytes();
	}
}