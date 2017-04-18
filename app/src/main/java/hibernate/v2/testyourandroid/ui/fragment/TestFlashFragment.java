package hibernate.v2.testyourandroid.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class TestFlashFragment extends BaseFragment {

	@BindView(R.id.back)
	RelativeLayout layout;

	@SuppressWarnings("deprecation")
	private Camera mCamera;
	@SuppressWarnings("deprecation")
	private Camera.Parameters mParams;

	private FlashLightUtilForL util;

	public TestFlashFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_test_flash, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mContext.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				init_camera2();
			} else {
				init_camera();
			}
		} else {
			C.openErrorDialog(mContext);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (util != null) {
				util.close();
			}
		} else {
			if (mCamera != null) {
				mCamera.release();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void init_camera() {
		try {
			mCamera = Camera.open(0);
		} catch (Exception e) {
			C.openErrorDialog(mContext);
		}

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void init_camera2() {
		try {
			util = new FlashLightUtilForL(mContext);
		} catch (Exception e) {
			C.openErrorDialog(mContext);
		}
	}

	@OnCheckedChanged(R.id.toggleButton)
	public void toggleButton(boolean isChecked) {
		if (isChecked) {
			openFlash();
		} else {
			closeFlash();
		}
	}

	@SuppressWarnings("deprecation")
	private void openFlash() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			util.turnOnFlashLight();
		} else {
			if (mCamera != null) {
				try {
					mParams = mCamera.getParameters();
					mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					mCamera.setParameters(mParams);
					mCamera.startPreview();
				} catch (Exception ignored) {
				}
			}
		}
		layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
	}

	@SuppressWarnings("deprecation")
	private void closeFlash() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			util.turnOffFlashLight();
		} else {
			if (mCamera != null) {
				try {
					mParams = mCamera.getParameters();
					mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(mParams);
					mCamera.stopPreview();
				} catch (Exception ignored) {
				}
			}
		}
		layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
	}

	@SuppressLint("NewApi")
	public class FlashLightUtilForL {
		private CameraCaptureSession mSession;
		private CaptureRequest.Builder mBuilder;
		private CameraDevice mCameraDevice;
		private CameraManager mCameraManager;

		@SuppressWarnings("MissingPermission")
		FlashLightUtilForL(Context context) throws Exception {
			mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
			//here to judge if flash is available
			CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics("0");
			boolean flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
			if (flashAvailable) {
				mCameraManager.openCamera("0", new MyCameraDeviceStateCallback(), null);
			}
		}

		class MyCameraDeviceStateCallback extends CameraDevice.StateCallback {

			@Override
			public void onOpened(@NonNull CameraDevice camera) {
				mCameraDevice = camera;
				//get builder
				try {
					mBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
					List<Surface> list = new ArrayList<>();
					SurfaceTexture mSurfaceTexture = new SurfaceTexture(1);
					Size size = getSmallestSize(mCameraDevice.getId());
					mSurfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
					Surface mSurface = new Surface(mSurfaceTexture);
					list.add(mSurface);
					mBuilder.addTarget(mSurface);
					camera.createCaptureSession(list, new MyCameraCaptureSessionStateCallback(), null);
				} catch (CameraAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDisconnected(@NonNull CameraDevice camera) {
			}

			@Override
			public void onError(@NonNull CameraDevice camera, int error) {
			}
		}

		private Size getSmallestSize(String cameraId) throws CameraAccessException {
			Size[] outputSizes = mCameraManager.getCameraCharacteristics(cameraId)
					.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
					.getOutputSizes(SurfaceTexture.class);
			if (outputSizes == null || outputSizes.length == 0) {
				throw new IllegalStateException(
						"Camera " + cameraId + "doesn't support any outputSize.");
			}
			Size chosen = outputSizes[0];
			for (Size s : outputSizes) {
				if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight()) {
					chosen = s;
				}
			}
			return chosen;
		}

		/**
		 * session callback
		 */
		class MyCameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

			@Override
			public void onConfigured(@NonNull CameraCaptureSession session) {
				mSession = session;
				try {
					mSession.setRepeatingRequest(mBuilder.build(), null, null);
				} catch (CameraAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onConfigureFailed(@NonNull CameraCaptureSession session) {
			}
		}

		void turnOnFlashLight() {
			try {
				mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
				mSession.setRepeatingRequest(mBuilder.build(), null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void turnOffFlashLight() {
			try {
				mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
				mSession.setRepeatingRequest(mBuilder.build(), null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void close() {
			if (mCameraDevice == null || mSession == null) {
				return;
			}
			mSession.close();
			mCameraDevice.close();
			mCameraDevice = null;
			mSession = null;
		}
	}
}
