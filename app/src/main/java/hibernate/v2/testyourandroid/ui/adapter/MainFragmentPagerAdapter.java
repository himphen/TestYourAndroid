package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.MainAboutFragment;
import hibernate.v2.testyourandroid.ui.fragment.MainTestFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private String tabTitles[];

	private Context mContext;

	/**
	 * Constructor of the class
	 */
	public MainFragmentPagerAdapter(FragmentManager fm, Context mContext) {
		super(fm);
		tabTitles = mContext.getResources().getStringArray(R.array.main_tab_title);
		this.mContext = mContext;
	}

	/**
	 * Returns the number of pages
	 */
	@Override
	public int getCount() {
		return 2;
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
				fragment = MainTestFragment.newInstance();
				break;
			case 1:
			default:
				fragment = new MainAboutFragment();
				break;
		}
		return fragment;
	}

	public View getTabView(int position) {
		View v = View.inflate(mContext, R.layout.custom_tab, null);
		TextView tv = v.findViewById(R.id.tabTitleTv);
		tv.setText(tabTitles[position]);
		return v;
	}
}