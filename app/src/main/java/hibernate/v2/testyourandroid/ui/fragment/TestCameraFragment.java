package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.view.TestCameraView;

/**
 * Created by himphen on 21/5/16.
 */
public class TestCameraFragment extends BaseFragment {

	private Camera mCamera = null;
	protected final String[] PERMISSION_NAME = {Manifest.permission.CAMERA};

	@BindView(R.id.cameraPreview)
	FrameLayout cameraPreview;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_test_camera, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (isPermissionsGranted(PERMISSION_NAME)) {
			openChooseCameraDialog();
		} else {
			requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCamera != null) {
			mCamera.release();
		}
	}

	private void openChooseCameraDialog() {
		int numberOfCamera = Camera.getNumberOfCameras();

		if (numberOfCamera == 1) {
			initCamera(0);
		} else {
			MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
					.title(R.string.dialog_camera_title)
					.items("Camera 1", "Camera 2")
					.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
						@Override
						public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
							initCamera(which);
							return false;
						}
					})
					.cancelable(false)
					.negativeText(R.string.ui_cancel)
					.onNegative(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							mContext.finish();
						}
					});
			dialog.show();
		}

	}

	private void initCamera(int which) {
		try {
			mCamera = Camera.open(which);//you can use open(int) to use different cameras
			Camera.Parameters params = mCamera.getParameters();
			if (params.getSupportedFocusModes().contains(
					Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(params);
			TestCameraView mCameraView = new TestCameraView(mContext, mCamera, which);
			cameraPreview.addView(mCameraView); //add the SurfaceView to the layout
		} catch (Exception e) {
			e.printStackTrace();
			C.openErrorDialog(mContext);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (hasAllPermissionsGranted(grantResults)) {
				openChooseCameraDialog();
			} else {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
