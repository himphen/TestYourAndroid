package hibernate.v2.testyourandroid.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import hibernate.v2.testyourandroid.C;

@SuppressLint("ViewConstructor")
@SuppressWarnings("deprecation")
public class TestCameraView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final double ASPECT_RATIO = 3.0 / 4.0;

	private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
	private static final int PREVIEW_SIZE_MAX_WIDTH = 640;
	private int mCameraId;

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Context mContext;

	public TestCameraView(Context context, Camera camera, int cameraId) {
		super(context);
		mContext = context;
		mCamera = camera;
		mCameraId = cameraId;
		//get the holder and set this class as the callback, so we can get camera data here
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
	}

	/**
	 * Measure the view and its content to determine the measured width and the
	 * measured height.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);

		if (width > height * ASPECT_RATIO) {
			width = (int) (height * ASPECT_RATIO + .5);
		} else {
			height = (int) (width / ASPECT_RATIO + .5);
		}

		setMeasuredDimension(width, height);
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		try {
			//when the surface is created, we can set the camera to draw images in this surfaceholder
			setCameraDisplayOrientation();

			Camera.Parameters parameters = mCamera.getParameters();

			Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
			Camera.Size bestPictureSize = determineBestPictureSize(parameters);

			parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
			parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

			mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		} catch (Exception ignored) {
			C.errorNoFeatureDialog(((Activity) mContext));
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
			//when the surface is created, we can set the camera to draw images in this surfaceholder
			setCameraDisplayOrientation();

			Camera.Parameters parameters = mCamera.getParameters();

			Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
			Camera.Size bestPictureSize = determineBestPictureSize(parameters);

			parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
			parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

			mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		} catch (Exception ignored) {
			C.errorNoFeatureDialog(((Activity) mContext));
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	}

	public void setCameraDisplayOrientation() {
		android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);
		int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}

	private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

		return determineBestSize(sizes, PREVIEW_SIZE_MAX_WIDTH);
	}

	private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

		return determineBestSize(sizes, PICTURE_SIZE_MAX_WIDTH);
	}

	private Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
		Camera.Size bestSize = null;

		for (Camera.Size currentSize : sizes) {
			boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
			boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
			boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

			if (isDesiredRatio && isInBounds && isBetterSize) {
				bestSize = currentSize;
			}
		}

		return bestSize;
	}
}