package hibernate.v2.testyourandroid.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoAndroidVersionFragment extends BaseFragment {

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private InfoItemAdapter adapter;

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
		String[] stringArray = getResources().getStringArray(R.array.info_android_version_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return Build.VERSION.RELEASE;
				case 1:
					return Build.VERSION.CODENAME;
				case 2:
					return Build.VERSION.INCREMENTAL;
				case 3:
					return String.valueOf(Build.VERSION.SDK_INT);
				case 4:
					return System.getProperty("os.arch");
				case 5:
					return System.getProperty("os.name");
				case 6:
					return System.getProperty("os.version");
				case 7:
					return System.getProperty("java.library.path");
				case 8:
					return System.getProperty("java.specification.version");
				case 9:
					return System.getProperty("java.specification.vendor");
				case 10:
					return System.getProperty("java.specification.name");
				case 11:
					return System.getProperty("java.vm.version");
				case 12:
					return System.getProperty("java.vm.vendor");
				case 13:
					return System.getProperty("java.vm.name");
				case 14:
					return System.getProperty("java.vm.specification.version");
				case 15:
					return System.getProperty("java.vm.specification.vendor");
				case 16:
					return System.getProperty("java.vm.specification.name");
				case 17:
					return System.getProperty("java.home");
				case 18:
					TimeZone tz = TimeZone.getDefault();
					return tz.getDisplayName(false, TimeZone.SHORT) + " " + tz.getID();
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
