package hibernate.v2.testyourandroid.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.adapter.MainFragmentPagerAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class MainFragment extends BaseFragment {

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	@BindView(R.id.tpi_header)
	TabLayout indicator;

	@BindView(R.id.vp_pages)
	ViewPager pager;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		adView = C.initAdView(mContext, adLayout);

		// Note that we are passing childFragmentManager, not FragmentManager
		MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getChildFragmentManager(), getContext());

		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(2);
		indicator.setupWithViewPager(pager);
		// Iterate over all tabs and set the custom view
		for (int i = 0; i < indicator.getTabCount(); i++) {
			TabLayout.Tab tab = indicator.getTabAt(i);
			if (tab != null) {
				tab.setCustomView(adapter.getTabView(i));
			}
		}
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.removeAllViews();
			adView.destroy();
		}
		super.onDestroy();
	}
}
