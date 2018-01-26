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
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoCPUFragment extends BaseFragment {

	private final String CPU_MIN = "CPU_MIN";
	private final String CPU_MAX = "CPU_MAX";
	private final String CPU_CUR = "CPU_CUR";

	private String[] memoryArray;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

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

		recyclerView.setAdapter(new InfoItemAdapter(list));
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return Integer.parseInt(getCPUInfo(CPU_MIN)) / 1000 + "MHz";
				case 1:
					return Integer.parseInt(getCPUInfo(CPU_MAX)) / 1000 + "MHz";
				case 2:
					return String.valueOf(getNumCores());
				case 3:
					long[] romMemory = getRomMemory();
					return
							memoryArray[0] + C.formatBitSize(romMemory[0]) + "\n"
									+ memoryArray[1] + C.formatBitSize(romMemory[1]) + "\n"
									+ memoryArray[2] + C.formatBitSize(romMemory[0] - romMemory[1]);
				case 4:
					return memoryArray[0] + C.formatBitSize(getSDCardMemory()[0]) + "\n"
							+ memoryArray[1] + C.formatBitSize(getSDCardMemory()[1]) + "\n"
							+ memoryArray[2] + C.formatBitSize(getSDCardMemory()[0] - getSDCardMemory()[1]);
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
			case CPU_MIN:
				filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
				break;
			case CPU_MAX:
				filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
				break;
			case CPU_CUR:
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

		if (activityManager != null) {
			activityManager.getMemoryInfo(memoryInfo);

			long totalMem = memoryInfo.totalMem;

			String text = "";
			text += memoryArray[0] + C.formatBitSize(totalMem) + "\n";
			text += memoryArray[1] + C.formatBitSize(memoryInfo.availMem) + "\n";
			text += memoryArray[2] + C.formatBitSize(totalMem - memoryInfo.availMem) + "\n";
			text += memoryArray[3] + C.formatBitSize(memoryInfo.threshold);

			return text;
		} else {
			return "";
		}
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
