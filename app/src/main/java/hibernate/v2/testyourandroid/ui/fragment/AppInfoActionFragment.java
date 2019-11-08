package hibernate.v2.testyourandroid.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.model.GridItem;
import hibernate.v2.testyourandroid.ui.adapter.GridItemAdapter;

public class AppInfoActionFragment extends BaseFragment {
	private Integer[] imageArray = {
			R.drawable.app_open, R.drawable.app_uninstall,
			R.drawable.app_settings, R.drawable.app_play_store};

	private String[] typeArray = {"open", "uninstall", "settings", "play_store"};

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;
	private AppItem appItem;

	public static AppInfoActionFragment newInstance(AppItem appItem) {
		AppInfoActionFragment fragment = new AppInfoActionFragment();
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
		init();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			appItem = bundle.getParcelable("APP");

			String[] stringArray = getResources().getStringArray(R.array.app_action_string_array);
			ArrayList<Integer> imageList = new ArrayList<>(Arrays.asList(imageArray));

			List<GridItem> list = new ArrayList<>();
			for (int i = 0; i < imageList.size(); i++) {
				list.add(new GridItem(stringArray[i], imageList.get(i), typeArray[i]));
			}

			int spanCount = 1;
			int columnCount = 3;
			GridLayoutManager man = new GridLayoutManager(getActivity(), spanCount);
			man.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					return 1;
				}
			});
			if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) {
				columnCount = 4;
			}

			GridItemAdapter.ItemClickListener mListener = new GridItemAdapter.ItemClickListener() {
				@Override
				public void onItemDetailClick(GridItem item) {
					Intent intent;
					switch (item.getActionType()) {
						case "uninstall":
							try {
								intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", appItem.getPackageName(), null));
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} catch (Exception e) {
								intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
							break;
						case "settings":
							try {
								intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
										Uri.fromParts("package", appItem.getPackageName(), null)
								);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} catch (Exception e) {
								try {
									ComponentName componentName = new ComponentName(
											"com.android.settings",
											"com.android.settings.applications.InstalledAppDetails");
									intent = new Intent();
									intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
									intent.setData(Uri.fromParts("package", appItem.getPackageName(), null));
									intent.setComponent(componentName);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
								} catch (Exception e1) {
									startActivity(new Intent(Settings.ACTION_SETTINGS));
								}
							}
							break;
						case "play_store":
							intent = new Intent(Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							try {
								intent.setData(Uri.parse("market://details?id=" + appItem.getPackageName()));
								startActivity(intent);
							} catch (ActivityNotFoundException e) {
								intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appItem.getPackageName()));
								startActivity(intent);
							}
							break;
						case "open":
							intent = mContext.getPackageManager().getLaunchIntentForPackage(appItem.getPackageName());
							if (intent != null) {
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} else {
								C.notAppFound(mContext);
							}
							break;
					}
				}
			};
			recyclerView.setHasFixedSize(true);
			recyclerView.setLayoutManager(new GridLayoutManager(mContext, columnCount));
			recyclerView.setAdapter(new GridItemAdapter(list, mListener));
		} else {
			C.notAppFound(mContext);
		}
	}
}