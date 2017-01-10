package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

public class AppInfoPackageFragment extends BaseFragment {
	private List<InfoItem> list = new ArrayList<>();
	private InfoItemAdapter adapter;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;
	private AppItem appItem;
	private PackageManager packageManager;
	private PackageInfo packageInfo;

	public AppInfoPackageFragment() {
		// Required empty public constructor
	}

	public static AppInfoPackageFragment newInstance(AppItem appItem) {
		AppInfoPackageFragment fragment = new AppInfoPackageFragment();
		Bundle args = new Bundle();
		args.putParcelable("APP", appItem);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		init();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			appItem = bundle.getParcelable("APP");

			try {
				packageManager = mContext.getPackageManager();
				packageInfo = packageManager.getPackageInfo(appItem.getPackageName(), 0);

				list = new ArrayList<>();
				String[] stringArray = getResources().getStringArray(R.array.app_package_string_array);

				for (int i = 0; i < stringArray.length; i++) {
					list.add(new InfoItem(stringArray[i], getData(i)));
				}

				adapter = new InfoItemAdapter(list);

				recyclerView.setAdapter(adapter);
			} catch (PackageManager.NameNotFoundException e) {
				C.notAppFound(mContext);
			}
		} else {
			C.notAppFound(mContext);
		}
	}

	private String getData(int j) {
		try {
			Date date;
			DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
			switch (j) {
				case 0:
					return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
							? "System Package" : "User-Installed Package";
				case 1:
					return packageInfo.packageName;
				case 2:
					return String.valueOf(packageInfo.versionCode);
				case 3:
					return packageInfo.versionName;
				case 4:
					date = new Date(packageInfo.firstInstallTime);
					return formatter.format(date);
				case 5:
					date = new Date(packageInfo.lastUpdateTime);
					return formatter.format(date);
				case 6:
					return packageInfo.applicationInfo.dataDir;
				case 7:
					return packageInfo.applicationInfo.publicSourceDir;
				case 8:
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						return String.valueOf(packageInfo.applicationInfo.minSdkVersion);
					} else {
						return getString(R.string.notsupport_android_24);
					}
				case 9:
					return String.valueOf(packageInfo.applicationInfo.targetSdkVersion);
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}