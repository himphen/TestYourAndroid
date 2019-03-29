package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppChooseItem;
import hibernate.v2.testyourandroid.ui.activity.AppListActivity;
import hibernate.v2.testyourandroid.ui.adapter.AppChooseAdapter;

import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_ALL;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_SYSTEM;
import static hibernate.v2.testyourandroid.ui.fragment.AppListFragment.ARG_APP_TYPE_USER;

public class AppChooseFragment extends BaseFragment {

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_info, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						RecyclerView.VERTICAL, false)
		);

		// User, System, All
		int[] countArray = {0, 0, 0};

		PackageManager packageManager = mContext.getPackageManager();
		List<PackageInfo> packs = C.getInstalledPackages(packageManager, 0);
		for (PackageInfo packageInfo : packs) {
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				countArray[0]++;
			} else {
				countArray[1]++;
			}
			countArray[2]++;
		}

		List<AppChooseItem> list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.app_choose_string_array);
		int[] intArray = {ARG_APP_TYPE_USER, ARG_APP_TYPE_SYSTEM, ARG_APP_TYPE_ALL};

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new AppChooseItem(stringArray[i], countArray[i] + " " + getString(R.string.app_package), intArray[i]));
		}

		AppChooseAdapter.ItemClickListener mListener = new AppChooseAdapter.ItemClickListener() {
			@Override
			public void onItemDetailClick(AppChooseItem appChooseItem) {
				Intent intent = new Intent().setClass(mContext, AppListActivity.class);
				intent.putExtra(ARG_APP_TYPE, appChooseItem.getAppType());
				startActivity(intent);
			}
		};
		recyclerView.setAdapter(new AppChooseAdapter(list, mListener));
	}
}