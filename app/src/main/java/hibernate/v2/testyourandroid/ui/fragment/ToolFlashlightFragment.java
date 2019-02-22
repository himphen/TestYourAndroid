package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class ToolFlashlightFragment extends BaseFragment {

	@BindView(R.id.turnSwitch)
	SwitchCompat turnSwitch;

	@SuppressWarnings("deprecation")
	private Camera mCamera;
	@SuppressWarnings("deprecation")
	private Camera.Parameters mParams;

	protected final String[] PERMISSION_NAME = {Manifest.permission.CAMERA};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_hardware_flashlight, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		closeFlash();
	}

	@OnCheckedChanged(R.id.turnSwitch)
	public void turnSwitch(boolean isChecked) {
		if (isChecked) {
			openFlash();
		} else {
			closeFlash();
		}
	}

	@SuppressWarnings("deprecation")
	private void openFlash() {
		if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (isPermissionsGranted(PERMISSION_NAME)) {
						CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
						try {
							assert cameraManager != null;
							String cameraId = cameraManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
							cameraManager.setTorchMode(cameraId, true);
						} catch (Exception e) {
							C.errorNoFeatureDialog(mContext);
							turnSwitch.setChecked(false);
						}
					} else {
						requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE);
						turnSwitch.setChecked(false);
					}
				} else {
					mCamera = Camera.open(0);
					mParams = mCamera.getParameters();
					mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					mCamera.setParameters(mParams);
					mCamera.startPreview();
				}
			} catch (Exception e) {
				C.errorNoFeatureDialog(mContext);
				turnSwitch.setChecked(false);
			}
		} else {
			C.errorNoFeatureDialog(mContext);
			turnSwitch.setChecked(false);
		}
	}

	@SuppressWarnings("deprecation")
	private void closeFlash() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
			try {
				assert cameraManager != null;
				String cameraId = cameraManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
				cameraManager.setTorchMode(cameraId, false);
			} catch (Exception ignored) {

			}
		} else {
			if (mCamera != null) {
				try {
					mParams = mCamera.getParameters();
					mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(mParams);
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				} catch (Exception ignored) {
				}
			}
		}

		turnSwitch.setChecked(false);
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
