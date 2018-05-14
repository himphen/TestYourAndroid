package hibernate.v2.testyourandroid.ui.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.view.TestMultiTouchView;

/**
 * Created by himphen on 21/5/16.
 */
public class TestMultiTouchFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return new TestMultiTouchView(mContext);
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN)) {
			C.errorNoFeatureDialog(mContext);
		} else {
			Toast.makeText(mContext, R.string.touch_message, Toast.LENGTH_LONG).show();
		}
	}

}
