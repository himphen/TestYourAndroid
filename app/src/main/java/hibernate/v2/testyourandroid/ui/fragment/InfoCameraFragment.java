package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
@SuppressWarnings("deprecation")
public class InfoCameraFragment extends BaseFragment {

	private final int FORMAT = 1;
	protected final String PERMISSION_NAME = Manifest.permission.CAMERA;
	private Camera mCamera;

	private InfoItemAdapter adapter;

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private int cameraId = 0;
	private Camera.Parameters mParameters;

	public InfoCameraFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_info_listview, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {
			openChooseCameraDialog();
		} else {
			requestPermissions(new String[]{PERMISSION_NAME}, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCamera != null) {
			initCamera(cameraId);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
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
		cameraId = which;

		try {
			mCamera = Camera.open(which);
			mParameters = mCamera.getParameters();
		} catch (Exception e) {
			e.printStackTrace();
			C.errorNoFeatureDialog(mContext);
			return;
		}

		List<InfoItem> list = new ArrayList<>();
		String[] stringArray = getResources().getStringArray(R.array.info_camera_string_array);

		for (int i = 0; i < stringArray.length; i++) {
			list.add(new InfoItem(stringArray[i], getData(i)));
		}

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);
	}

	private String integerListToString(List<Integer> list, int type) {
		if (list == null)
			return "Not supported";
		StringBuilder tempList = new StringBuilder();
		String format = "";
		for (Integer element : list) {
			if (type == FORMAT) {
				switch (element) {
					case 256:
						format = "JPEG";
						break;
					case 16:
						format = "NV16";
						break;
					case 17:
						format = "NV21";
						break;
					case 4:
						format = "RGB_565";
						break;
					case 0:
						format = "UNKNOWN";
						break;
					case 20:
						format = "YUY2";
						break;
					case 842094169:
						format = "YV12";
						break;
					default:
						format = "UNKNOWN";
						break;
				}
			}
			tempList.append(format).append("\n");
		}
		return tempList.substring(0, tempList.length() - 1);
	}

	private String listToString(List<String> list) {
		if (list == null)
			return "Not supported";
		StringBuilder tempList = new StringBuilder();
		for (String element : list)
			tempList.append(element).append("\n");
		return tempList.substring(0, tempList.length() - 1);
	}

	private String sizeListToString(List<Camera.Size> list) {
		StringBuilder tempList = new StringBuilder();
		for (Camera.Size element : list)
			tempList.append(element.width).append(" X ").append(element.height).append("\n");
		return tempList.substring(0, tempList.length() - 1);
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
				case 1:
					DisplayMetrics displayMetrics = new DisplayMetrics();
					WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
					if (windowManager == null) {
						throw new Exception();
					}
					windowManager.getDefaultDisplay().getMetrics(displayMetrics);
					int screenWidth = displayMetrics.widthPixels;
					int screenHeight = displayMetrics.heightPixels;
					return j == 0 ? screenWidth + " px" : screenHeight + " px";
				case 2:
					List<Camera.Size> supportedPictureSizes = mParameters
							.getSupportedPictureSizes();
					return sizeListToString(supportedPictureSizes);
				case 3:
					List<String> supportedAntibanding = mParameters
							.getSupportedAntibanding();
					return listToString(supportedAntibanding);
				case 4:
					List<String> supportedColorEffects = mParameters
							.getSupportedColorEffects();
					return listToString(supportedColorEffects);
				case 5:
					List<String> supportedFlashModes = mParameters
							.getSupportedFlashModes();
					return listToString(supportedFlashModes);
				case 6:
					List<String> supportedFocusModes = mParameters
							.getSupportedFocusModes();
					return listToString(supportedFocusModes);
				case 7:
					List<String> supportedWhiteBalance = mParameters
							.getSupportedWhiteBalance();
					return listToString(supportedWhiteBalance);
				case 8:
					List<String> supportedSceneModes = mParameters
							.getSupportedSceneModes();
					return listToString(supportedSceneModes);
				case 9:
					List<Integer> supportedPictureFormats = mParameters
							.getSupportedPictureFormats();
					return integerListToString(supportedPictureFormats, FORMAT);
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
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
