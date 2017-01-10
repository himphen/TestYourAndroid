package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.fragment.MainAboutFragment;
import hibernate.v2.testyourandroid.ui.fragment.MainGridFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private String tabTitles[];

	private final int PAGE_COUNT = 3;
	private Context context;

	/**
	 * Constructor of the class
	 */
	public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		tabTitles = context.getResources().getStringArray(R.array.main_tab_title);
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
				fragment = MainGridFragment.newInstance(MainGridFragment.ARG_GRID_TYPE_TEST);
				break;
			case 1:
				fragment = MainGridFragment.newInstance(MainGridFragment.ARG_GRID_TYPE_INFO);
				break;
			case 2:
				fragment = new MainAboutFragment();
				break;
			default:
				fragment = null;
		}
		return fragment;
	}

	public View getTabView(int position) {
		View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
		TextView tv = (TextView) v.findViewById(R.id.tabTitleTv);
		tv.setText(tabTitles[position]);
		return v;
	}
}