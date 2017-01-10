package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;
import hibernate.v2.testyourandroid.ui.adapter.AppInfoFragmentPagerAdapter;

public class AppInfoFragment extends BaseFragment {

	private AdView adView;

	@BindView(R.id.adLayout)
	RelativeLayout adLayout;

	@BindView(R.id.tpi_header)
	TabLayout indicator;

	@BindView(R.id.vp_pages)
	ViewPager pager;

	private AppItem appItem;

	public static AppInfoFragment newInstance(AppItem appItem) {
		AppInfoFragment fragment = new AppInfoFragment();
		Bundle args = new Bundle();
		args.putParcelable("APP", appItem);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_info, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			appItem = bundle.getParcelable("APP");

			// Note that we are passing childFragmentManager, not FragmentManager
			AppInfoFragmentPagerAdapter adapter = new AppInfoFragmentPagerAdapter(getChildFragmentManager(), mContext, appItem);

			pager.setAdapter(adapter);
			pager.setCurrentItem(0);
			pager.setOffscreenPageLimit(2);
			indicator.setupWithViewPager(pager);
			// Iterate over all tabs and set the custom view
			for (int i = 0; i < indicator.getTabCount(); i++) {
				TabLayout.Tab tab = indicator.getTabAt(i);
				if (tab != null) {
					tab.setCustomView(adapter.getTabView(i));
				}
			}
		} else {
			Toast.makeText(mContext, R.string.app_notfound, Toast.LENGTH_LONG).show();
			mContext.finish();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				adView = C.initAdView(mContext, adLayout);
			}
		}, 1000);
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.removeAllViews();
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (appItem != null) {
				mContext.getPackageManager().getPackageInfo(appItem.getPackageName(), PackageManager.GET_ACTIVITIES);
			}
		} catch (Exception e) {
			C.notAppFound(mContext);
		}
	}
}