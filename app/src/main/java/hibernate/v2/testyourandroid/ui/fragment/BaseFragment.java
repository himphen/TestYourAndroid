package hibernate.v2.testyourandroid.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

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
		return C.isPermissionsGranted(mContext, permissions);
	}

	protected boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
		return C.hasAllPermissionsGranted(grantResults);
	}

	protected Snackbar showSnackbar(View view, int stringRid) {
		Snackbar snackbar = Snackbar
				.make(view, stringRid, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		sbView.setBackgroundResource(R.color.primary_dark);
		((TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text)).setTextColor(Color.WHITE);
		snackbar.show();
		return snackbar;
	}

	protected Snackbar showSnackbar(View view, String string) {
		Snackbar snackbar = Snackbar
				.make(view, string, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		sbView.setBackgroundResource(R.color.primary_dark);
		((TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text)).setTextColor(Color.WHITE);
		snackbar.show();
		return snackbar;
	}

	protected Snackbar setBlueSnackbar(Snackbar snackbar) {
		View sbView = snackbar.getView();
		sbView.setBackgroundResource(R.color.primary_dark);
		((TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text)).setTextColor(Color.WHITE);
		((TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_action)).setTextColor(getResources().getColor(R.color.gold));
		return snackbar;
	}
}
