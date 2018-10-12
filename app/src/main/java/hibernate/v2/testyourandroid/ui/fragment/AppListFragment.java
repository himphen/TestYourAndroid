package hibernate.v2.testyourandroid.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.ui.activity.AppDetailsActivity;
import hibernate.v2.testyourandroid.ui.adapter.AppItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class AppListFragment extends BaseFragment {

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private AppItemAdapter adapter;
	private ArrayList<AppItem> appList = new ArrayList<>();

	public static final String ARG_APP_TYPE = "appType";
	public static final int ARG_APP_TYPE_USER = 0;
	public static final int ARG_APP_TYPE_SYSTEM = 1;
	public static final int ARG_APP_TYPE_ALL = 2;

	private int appType = ARG_APP_TYPE_USER;

	public static AppListFragment newInstance(int sensorType) {
		AppListFragment fragment = new AppListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_APP_TYPE, sensorType);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			appType = getArguments().getInt(ARG_APP_TYPE, ARG_APP_TYPE_USER);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview_scrollbar, container, false);
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
		recyclerView.setVerticalScrollBarEnabled(true);
		init();
	}

	private void init() {
		loadAppList();
	}

	@SuppressLint("StaticFieldLeak")
	private void loadAppList() {
		new AsyncTask<Void, Void, Void>() {
			private MaterialDialog dialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new MaterialDialog.Builder(mContext)
						.content(R.string.ui_loading)
						.progress(true, 0)
						.cancelable(false)
						.show();
			}


			@Override
			protected Void doInBackground(Void... voids) {
				PackageManager packageManager = mContext.getPackageManager();
				List<PackageInfo> packs = getInstalledPackages(packageManager, PackageManager.GET_PERMISSIONS);
				for (PackageInfo packageInfo : packs) {
					if (appType == ARG_APP_TYPE_USER) {
						if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
							continue;
						}
					} else if (appType == ARG_APP_TYPE_SYSTEM) {
						if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
							continue;
						}
					}

					AppItem appItem = new AppItem();
					appItem.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
					appItem.setSourceDir(packageInfo.applicationInfo.dataDir);
					appItem.setPackageName(packageInfo.packageName);
					appItem.setVersionCode(String.valueOf(packageInfo.versionCode));
					appItem.setVersionName(packageInfo.versionName);
					appItem.setFirstInstallTime(packageInfo.firstInstallTime);
					appItem.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
					appItem.setSystemApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);

					appList.add(appItem);
				}

				Collections.sort(appList, new Comparator<AppItem>() {
					@Override
					public int compare(AppItem item1, AppItem item2) {
						return item1.getAppName().toLowerCase().compareTo(item2.getAppName().toLowerCase());
					}
				});
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				dialog.dismiss();
				refreshList();
			}
		}.execute();
	}

	private void refreshList() {
		AppItemAdapter.ItemClickListener mListener = new AppItemAdapter.ItemClickListener() {
			@Override
			public void onItemDetailClick(final AppItem infoAppItem) {
				Intent intent = new Intent().setClass(mContext, AppDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("APP", infoAppItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		};
		adapter = new AppItemAdapter(appList, mListener);
		recyclerView.setAdapter(adapter);
	}

	public List<PackageInfo> getInstalledPackages(PackageManager packageManager, int flags) {
		try {
			return packageManager.getInstalledPackages(flags);
		} catch (Exception ignored) {
			// we don't care why it didn't succeed. We'll do it using an alternative way instead
		}
		// use fallback:
		List<PackageInfo> result = new ArrayList<>();
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec("pm list packages");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				final String packageName = line.substring(line.indexOf(':') + 1);
				final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, flags);
				result.add(packageInfo);
			}
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
}
