package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.StringUtils;

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
	private final String NO_GROUP = "Ungrouped Permissions";

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private AppItem appItem;

	private HashMap<String, ArrayList<AppPermissionItem>> map = new HashMap<>();

	public static AppInfoPermissionFragment newInstance(AppItem appItem) {
		AppInfoPermissionFragment fragment = new AppInfoPermissionFragment();
		Bundle args = new Bundle();
		args.putParcelable("APP", appItem);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						RecyclerView.VERTICAL, false)
		);
		init();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			appItem = bundle.getParcelable("APP");

			List<InfoItem> list = new ArrayList<>();

			try {
				PackageManager packageManager = mContext.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(appItem.getPackageName(), PackageManager.GET_PERMISSIONS);

				/* Get Permissions */
				String[] requestedPermissions = packageInfo.requestedPermissions;

				if (requestedPermissions != null) {
					for (String requestedPermission : requestedPermissions) {
						String permissionGroupLabel = NO_GROUP;
						String permissionLabel = "";
						String permissionDescription = "";

						try {
							PermissionInfo permissionInfo = packageManager.getPermissionInfo(requestedPermission, 0);

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


						} catch (Exception e) {
							permissionLabel = requestedPermission;
						}

						permissionLabel = StringUtils.upperFirstLetter(permissionLabel);

						AppPermissionItem appPermissionItem = new AppPermissionItem(permissionGroupLabel, permissionLabel, permissionDescription);

						if (!map.containsKey(permissionGroupLabel)) {
							map.put(permissionGroupLabel, new ArrayList<AppPermissionItem>());
						}

						ArrayList<AppPermissionItem> arrayList = map.get(permissionGroupLabel);
						arrayList.add(appPermissionItem);
						map.put(permissionGroupLabel, arrayList);
					}
				}
				ArrayList<String> sortedKeys = new ArrayList<>(map.keySet());
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

					StringBuilder permissionLabel = new StringBuilder();

					for (AppPermissionItem s : value) {
						permissionLabel.append(s.getPermissionLabel()).append("\n");
					}

					list.add(new InfoItem(permissionGroupLabel, permissionLabel.toString().trim()));
				}

			} catch (Exception e) {
				list.add(new InfoItem("Fail to fetch the permissions", "Error: -1034"));
			}

			InfoItemAdapter adapter = new InfoItemAdapter(list);

			recyclerView.setAdapter(adapter);
		} else {
			C.notAppFound(mContext);
		}
	}
}