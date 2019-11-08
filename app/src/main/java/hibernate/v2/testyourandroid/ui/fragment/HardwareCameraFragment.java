package hibernate.v2.testyourandroid.ui.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;
import com.otaliastudios.cameraview.size.AspectRatio;
import com.otaliastudios.cameraview.size.SizeSelectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class HardwareCameraFragment extends BaseFragment {

	@BindView(R.id.cameraView)
	CameraView cameraView;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_hardware_camera, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	private void openChooseCameraDialog() {
		int numberOfCamera = Camera.getNumberOfCameras();

		if (numberOfCamera == 1) {
			initCamera(true);
		} else {
			MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
					.title(R.string.dialog_camera_title)
					.items("Camera 1", "Camera 2")
					.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
						@Override
						public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
							initCamera(which == 0);

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

	private void initCamera(boolean isCameraFacingBack) {
		CameraLogger.registerLogger(new CameraLogger.Logger() {
			@Override
			public void log(@CameraLogger.LogLevel int level, @NonNull String tag, @NonNull String message, @Nullable Throwable throwable) {
				if (level == CameraLogger.LEVEL_ERROR) {
					if (throwable != null) {
						C.errorNoFeatureDialog(mContext);
					}
				}
			}
		});
		if (isCameraFacingBack) {
			cameraView.setFacing(Facing.BACK);
		} else {
			cameraView.setFacing(Facing.FRONT);
		}
		cameraView.setMode(Mode.PICTURE);
		cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
		cameraView.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.ZOOM);
		cameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(4, 3), 0));
		cameraView.open();
	}

	@Override
	public void onResume() {
		super.onResume();
		openChooseCameraDialog();
	}

	@Override
	public void onPause() {
		super.onPause();
		cameraView.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cameraView.destroy();
	}
}
