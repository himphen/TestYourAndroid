package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.ui.activity.AppInfoActivity;
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

	public AppListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			appType = getArguments().getInt(ARG_APP_TYPE, ARG_APP_TYPE_USER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview_scrollbar, container, false);
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
		recyclerView.setVerticalScrollBarEnabled(true);
		init();
	}

	private void init() {
		loadAppList();
	}

	private void loadAppList() {
		new AsyncTask<Void, Void, Void>() {
			private MaterialDialog dialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog = new MaterialDialog.Builder(mContext)
						.content(R.string.loading)
						.progress(true, 0)
						.cancelable(false)
						.show();
			}


			@Override
			protected Void doInBackground(Void... voids) {
				PackageManager packageManager = mContext.getPackageManager();
				List<PackageInfo> packs = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
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
				Intent intent = new Intent().setClass(mContext, AppInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("APP", infoAppItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		};
		adapter = new AppItemAdapter(appList, mListener);
		recyclerView.setAdapter(adapter);

		new DragScrollBar(mContext, recyclerView, true)
				.setDraggableFromAnywhere(true)
				.setHandleColourRes(R.color.primary)
				.setIndicator(new AlphabetIndicator(mContext), true);
	}
}
