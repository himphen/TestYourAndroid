package hibernate.v2.testyourandroid.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoHeader;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adaptor.InfoItemAdaptor;

/**
 * Created by himphen on 21/5/16.
 */
public class InfoAndroidVersionFragment extends BaseFragment {

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private InfoItemAdaptor adapter;

	public InfoAndroidVersionFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
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
		List<InfoItem> list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_android_version_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdaptor(list);
		adapter.setHeader(new InfoHeader(mContext.getTitle().toString()));
		recyclerView.setAdapter(adapter);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return Build.VERSION.RELEASE;
				case 1:
					return Build.VERSION.CODENAME;
				case 2:
					return Build.VERSION.INCREMENTAL;
				case 3:
					return String.valueOf(Build.VERSION.SDK_INT);
				case 4:
					return System.getProperty("os.arch");
				case 5:
					return System.getProperty("os.name");
				case 6:
					return System.getProperty("os.version");
				case 7:
					return Build.BOARD;
				case 8:
					return Build.RADIO;
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
