package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.ui.fragment.AppInfoActionFragment;
import hibernate.v2.testyourandroid.ui.fragment.AppInfoPackageFragment;
import hibernate.v2.testyourandroid.ui.fragment.AppInfoPermissionFragment;

public class AppInfoFragmentPagerAdapter extends FragmentPagerAdapter {

	private AppItem appItem;
	private String tabTitles[];

	private Context mContext;

	/**
	 * Constructor of the class
	 */
	public AppInfoFragmentPagerAdapter(FragmentManager fm, Context mContext, AppItem appItem) {
		super(fm);
		tabTitles = mContext.getResources().getStringArray(R.array.app_info_tab_title);
		this.mContext = mContext;
		this.appItem = appItem;
	}

	/**
	 * Returns the number of pages
	 */
	@Override
	public int getCount() {
		return 3;
	}

	/**
	 * This method will be invoked when a page is requested to create
	 */
	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		switch (position) {
			/* Android tab is selected */
			case 0:
				fragment = AppInfoActionFragment.newInstance(appItem);
				break;
			case 1:
				fragment = AppInfoPackageFragment.newInstance(appItem);
				break;
			case 2:
				fragment = AppInfoPermissionFragment.newInstance(appItem);
				break;
			default:
				fragment = null;
		}
		return fragment;
	}

	public View getTabView(int position) {
		View v = View.inflate(mContext, R.layout.custom_tab_inverse, null);
		TextView tv = v.findViewById(R.id.tabTitleTv);
		tv.setText(tabTitles[position]);
		return v;
	}
}