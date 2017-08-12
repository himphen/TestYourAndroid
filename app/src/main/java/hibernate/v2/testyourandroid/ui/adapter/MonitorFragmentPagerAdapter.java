package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.MonitorCpuFragment;
import hibernate.v2.testyourandroid.ui.fragment.MonitorMemoryFragment;
import hibernate.v2.testyourandroid.ui.fragment.MonitorNetworkFragment;

public class MonitorFragmentPagerAdapter extends FragmentPagerAdapter {

	private String tabTitles[];

	private final int PAGE_COUNT = 3;
	private Context context;

	/**
	 * Constructor of the class
	 */
	public MonitorFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		tabTitles = context.getResources().getStringArray(R.array.test_monitor_tab_title);
		this.context = context;
	}

	/**
	 * Returns the number of pages
	 */
	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	/**
	 * This method will be invoked when a page is requested to create
	 */
	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		switch (position) {
			/** Android tab is selected */
			case 0:
				fragment = new MonitorCpuFragment();
				break;
			case 1:
				fragment = new MonitorNetworkFragment();
				break;
			case 2:
				fragment = new MonitorMemoryFragment();
				break;
			default:
				fragment = null;
		}
		return fragment;
	}

	public View getTabView(int position) {
		View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
		TextView tv = v.findViewById(R.id.tabTitleTv);
		tv.setText(tabTitles[position]);
		return v;
	}
}