package hibernate.v2.testyourandroid.ui.fragment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class BaseFragment extends Fragment {

	protected Activity mContext;
	protected static final int DELAY_AD_LAYOUT = 200;
	protected final int PERMISSION_REQUEST_CODE = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	protected boolean isPermissionsGranted(String[] permissions) {
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	protected boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

}
