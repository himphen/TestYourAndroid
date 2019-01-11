package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class ToolQRScannerFragment extends BaseFragment {

	protected final String[] PERMISSION_NAME = {Manifest.permission.CAMERA};

	@BindView(R.id.scannerView)
	CodeScannerView scannerView;

	private CodeScanner mCodeScanner;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tool_qr_scanner, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mCodeScanner = new CodeScanner(mContext, scannerView);
		mCodeScanner.setDecodeCallback(new DecodeCallback() {
			@Override
			public void onDecoded(@NonNull Result result) {
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						FragmentManager manager = getActivity().getSupportFragmentManager();
						FragmentTransaction transaction = manager.beginTransaction();

						ToolQRScannerSuccessFragment fragment = ToolQRScannerSuccessFragment.newInstance(
								result.getText(),
								result.getBarcodeFormat().name()
						);
						transaction.replace(R.id.container, fragment);
						transaction.isAddToBackStackAllowed();
						transaction.addToBackStack(null);
						transaction.commit();
					}
				});
			}
		});
		scannerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mCodeScanner.startPreview();
			}
		});

		if (!isPermissionsGranted(PERMISSION_NAME)) {
			requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			if (isPermissionsGranted(PERMISSION_NAME)) {
				mCodeScanner.startPreview();
			}
		} catch (Exception e) {
			C.logException(e);
			C.errorNoFeatureDialog(mContext);
		}
	}

	@Override
	public void onPause() {
		mCodeScanner.releaseResources();
		super.onPause();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (!hasAllPermissionsGranted(grantResults)) {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
