package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.model.AppPermissionItem;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

public class AppInfoPermissionFragment extends BaseFragment {
	private List<InfoItem> list = new ArrayList<>();
	private InfoItemAdapter adapter;
	private final String NO_GROUP = "Ungrouped Permissions";

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private AppItem appItem;

	private HashMap<String, ArrayList<AppPermissionItem>> map = new HashMap<>();

	public AppInfoPermissionFragment() {
		// Required empty public constructor
	}

	public static AppInfoPermissionFragment newInstance(AppItem appItem) {
		AppInfoPermissionFragment fragment = new AppInfoPermissionFragment();
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

			list = new ArrayList<>();

			try {
				PackageManager packageManager = mContext.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(appItem.getPackageName(), PackageManager.GET_PERMISSIONS);

				/* Get Permissions */
				String[] requestedPermissions = packageInfo.requestedPermissions;

				if (requestedPermissions != null) {
					for (String requestedPermission : requestedPermissions) {
						PermissionInfo permissionInfo = packageManager.getPermissionInfo(requestedPermission, 0);

						String permissionGroupLabel = NO_GROUP;
						String permissionLabel = "";
						String permissionDescription = "";

						try {
							PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
							permissionGroupLabel = permissionGroupInfo.loadLabel(packageManager).toString();
						} catch (PackageManager.NameNotFoundException | NullPointerException ignored) {
						}
						try {
							permissionLabel = permissionInfo.loadLabel(packageManager).toString();
						} catch (NullPointerException ignored) {
						}
						try {
							permissionDescription = permissionInfo.loadDescription(packageManager).toString();
						} catch (NullPointerException ignored) {
						}

						permissionLabel = WordUtils.capitalize(permissionLabel);

						AppPermissionItem appPermissionItem = new AppPermissionItem(permissionGroupLabel, permissionLabel, permissionDescription);

						if (!map.containsKey(permissionGroupLabel)) {
							map.put(permissionGroupLabel, new ArrayList<AppPermissionItem>());
						}

						ArrayList<AppPermissionItem> arrayList = map.get(permissionGroupLabel);
						arrayList.add(appPermissionItem);
						map.put(permissionGroupLabel, arrayList);
					}
				}
				//noinspection unchecked
				ArrayList<String> sortedKeys = new ArrayList(map.keySet());
				Collections.sort(sortedKeys);

				sortedKeys.remove(NO_GROUP);
				sortedKeys.add(NO_GROUP);

				for (Object key : sortedKeys) {
					String permissionGroupLabel = (String) key;
					ArrayList<AppPermissionItem> value = map.get(permissionGroupLabel);

					Collections.sort(value, new Comparator<AppPermissionItem>() {
						@Override
						public int compare(AppPermissionItem item1, AppPermissionItem item2) {
							return item1.getPermissionLabel().compareTo(item2.getPermissionLabel());
						}
					});

					String permissionLabel = "";

					for (AppPermissionItem s : value) {
						permissionLabel += s.getPermissionLabel() + "\n";
					}

					list.add(new InfoItem(permissionGroupLabel, permissionLabel.trim()));
				}

			} catch (Exception e) {
				list.add(new InfoItem("Fail to fetch the permissions", "Error: -1034"));
			}

			adapter = new InfoItemAdapter(list);

			recyclerView.setAdapter(adapter);
		} else {
			C.notAppFound(mContext);
		}
	}
}