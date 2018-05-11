package hibernate.v2.testyourandroid.ui.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoHeader;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adaptor.InfoItemAdaptor;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoCPUFragment extends BaseFragment {

	private final String CPUMIN = "CPUMIN";
	private final String CPUMAX = "CPUMAX";
	private final String CPUCUR = "CPUCUR";

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private String[] memoryArray;

	public InfoCPUFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
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

	private void init() {

		List<InfoItem> list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_cpu_string_array);
		memoryArray = getResources().getStringArray(R.array.memory_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		InfoItemAdaptor adapter = new InfoItemAdaptor(list);
		adapter.setHeader(new InfoHeader(mContext.getTitle().toString()));
		recyclerView.setAdapter(adapter);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return Integer.parseInt(getCPUInfo(CPUMIN)) / 1000 + "MHz";
				case 1:
					return Integer.parseInt(getCPUInfo(CPUMAX)) / 1000 + "MHz";
				case 2:
					return String.valueOf(getNumCores());
				case 3:
					return
							memoryArray[0] + formatSize(getRomMemory()[0]) + "\n"
									+ memoryArray[1] + formatSize(getRomMemory()[1]) + "\n"
									+ memoryArray[2] + formatSize(getRomMemory()[0] - getRomMemory()[1]);
				case 4:
					return memoryArray[0] + formatSize(getSDCardMemory()[0]) + "\n"
							+ memoryArray[1] + formatSize(getSDCardMemory()[1]) + "\n"
							+ memoryArray[2] + formatSize(getSDCardMemory()[0] - getSDCardMemory()[1]);
				case 5:
					return getRamMemory();
				case 6:
					return getAllMemory();
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}

	private String formatSize(long size) {
		String suffix = null;
		float fSize;

		if (size >= 1024) {
			suffix = " KBytes";
			fSize = size / 1024;
			if (fSize >= 1024) {
				suffix = " MBytes";
				fSize /= 1024;
			}
			if (fSize >= 1024) {
				suffix = " GBytes";
				fSize /= 1024;
			}
		} else {
			fSize = size;
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	private String getAllMemory() {
		String str1 = "/proc/meminfo";
		String str2;
		StringBuilder str3 = new StringBuilder();
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				str3.append(str2).append("\n");
			}
			str3 = new StringBuilder(str3.substring(0, str3.length() - 1));
			localBufferedReader.close();
		} catch (IOException ignored) {
		}
		str3 = new StringBuilder(str3.toString().replaceAll(" ", ""));
		str3 = new StringBuilder(str3.toString().replaceAll(":", ": "));
		return str3.toString();
	}

	private String getCPUInfo(String type) {
		String filename = "";
		int single = 0;
		switch (type) {
			case CPUMIN:
				filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
				break;
			case CPUMAX:
				filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
				break;
			case CPUCUR:
				filename = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
				break;
			default:
				single = 1;
				break;
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
			if (single == 1) {
				sd.append("\n");
			}
		}
		return sd.toString();
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

	private String getRamMemory() {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		long totalMem = memoryInfo.totalMem;

		String text = "";
		text += memoryArray[0] + formatSize(totalMem) + "\n";
		text += memoryArray[1] + formatSize(memoryInfo.availMem) + "\n";
		text += memoryArray[2] + formatSize(totalMem - memoryInfo.availMem) + "\n";
		text += memoryArray[3] + formatSize(memoryInfo.threshold);
		return text;
	}

	private long[] getRomMemory() {
		long[] romInfo = new long[2];
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());

		romInfo[0] = stat.getBlockSizeLong() * stat.getBlockCountLong();
		romInfo[1] = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
		return romInfo;
	}

	private long[] getSDCardMemory() {
		long[] sdCardInfo = new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			sdCardInfo[0] = stat.getBlockSizeLong() * stat.getBlockCountLong();
			sdCardInfo[1] = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
		}
		return sdCardInfo;
	}
}
