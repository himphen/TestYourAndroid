package hibernate.v2.testyourandroid.ui.fragment;

import android.graphics.Color;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;

public class MonitorCpuFragment extends BaseFragment {

	private final int CPU_MIN = 0;
	private final int CPU_MAX = 1;
	private final int CPU_CURRENT = 2;

	private GraphViewSeries series;
	private double lastXValue = 0;

	private Handler mHandler = new Handler();

	private Runnable timer = new Runnable() {
		@Override
		public void run() {
			lastXValue += 1;
			speedText.setText(getCPU(CPU_CURRENT) + " MHz");
			series.appendData(new GraphViewData(lastXValue, getCPU(CPU_CURRENT)),
					true, 100);
			mHandler.postDelayed(timer, 1000);
		}
	};

	@BindView(R.id.graph2)
	LinearLayout layout;
	@BindView(R.id.coreText)
	TextView coreText;
	@BindView(R.id.minText)
	TextView minText;
	@BindView(R.id.maxText)
	TextView maxText;
	@BindView(R.id.speedText)
	TextView speedText;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor_cpu, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		speedText.setText(getCPU(CPU_CURRENT) + " MHz");
		coreText.setText(String.valueOf(getNumCores()));
		minText.setText(getCPU(CPU_MIN) + " MHz");
		maxText.setText(getCPU(CPU_MAX) + " MHz");
		GraphView graphView = new LineGraphView(mContext, "");
		graphView.setShowHorizontalLabels(false);
		GraphViewData[] data = new GraphViewData[]{new GraphViewData(0, 0)};
		series = new GraphViewSeries("", new GraphViewSeriesStyle(
				Color.parseColor("#33B5E5"), 3), data);
		graphView.addSeries(series);
		graphView.setGraphViewStyle(new GraphViewStyle(Color.GRAY, Color.GRAY,
				Color.LTGRAY));
		graphView.setManualYAxisBounds(getCPU(CPU_MAX), 0);
		graphView.setViewPort(0, 36);
		graphView.setScalable(true);
		graphView.setScrollable(true);
		graphView.setDisableTouch(true);
		layout.addView(graphView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.postDelayed(timer, 1000);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(timer);
	}

	private int getCPU(int type) {
		String filename = "";
		if (type == CPU_MIN) {
			filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
		} else if (type == CPU_MAX) {
			filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
		} else if (type == CPU_CURRENT) {
			filename = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
		}
		ArrayList<String> list = new ArrayList<>();
		if (new File(filename).exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(
						filename)));
				String aLine;
				while ((aLine = br.readLine()) != null) {
					list.add(aLine);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuilder sd = new StringBuilder();
		for (String e : list) {
			sd.append(e);
		}
		try {
			return Integer.valueOf(sd.toString()) / 1000;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				return Pattern.matches("cpu[0-9]", pathname.getName());
			}
		}
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Default to return 1 core
			return 1;
		}
	}
}