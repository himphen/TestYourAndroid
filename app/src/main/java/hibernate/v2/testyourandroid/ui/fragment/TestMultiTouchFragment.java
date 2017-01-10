package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.custom.TestMultiTouchView;

/**
 * Created by himphen on 21/5/16.
 */
public class TestMultiTouchFragment extends BaseFragment {

	public TestMultiTouchFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return new TestMultiTouchView(mContext);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN)) {
			C.openErrorDialog(mContext);
		} else {
			Toast.makeText(mContext, R.string.touch_message, Toast.LENGTH_LONG).show();
		}
	}

}
