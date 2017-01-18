package hibernate.v2.testyourandroid.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	protected Activity mContext;
	protected static final int DELAY_AD_LAYOUT = 200;
	protected final int PERMISSION_REQUEST_CODE = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

	}

}
