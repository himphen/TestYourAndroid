package hibernate.v2.testyourandroid.ui.hardware

import android.hardware.Camera
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_hardware_camera.*

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class HardwareCameraFragment : BaseFragment(R.layout.fragment_hardware_camera) {

    private fun openChooseCameraDialog() {
        val numberOfCamera = Camera.getNumberOfCameras()
        if (numberOfCamera == 1) {
            initCamera(true)
        } else {
            context?.let {
                MaterialDialog(it)
                    .title(R.string.dialog_camera_title)
                    .listItemsSingleChoice(
                        items = arrayListOf("Camera 1", "Camera 2"),
                        waitForPositiveButton = false
                    ) { dialog, index, _ ->
                        initCamera(index == 0)
                        dialog.dismiss()
                    }
                    .cancelable(false)
                    .negativeButton(R.string.ui_cancel) { dialog ->
                        UtilHelper.scanForActivity(dialog.context)?.finish()
                    }
                    .show()
            }
        }
    }

    private fun initCamera(isCameraFacingBack: Boolean) {
        CameraLogger.registerLogger { level, _, _, throwable ->
            if (level == CameraLogger.LEVEL_ERROR) {
                if (throwable != null) {
                    errorNoFeatureDialog(context)
                }
            }
        }
        try {
            cameraView?.facing = if (isCameraFacingBack) Facing.BACK else Facing.FRONT
            cameraView?.mode = Mode.PICTURE
            cameraView?.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
            cameraView?.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.ZOOM)
            cameraView?.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(4, 3), 0f))
            cameraView?.open()
        } catch (e: Exception) {
            errorNoFeatureDialog(context)
        }
    }

    override fun onResume() {
        super.onResume()
        openChooseCameraDialog()
    }

    override fun onPause() {
        super.onPause()
        cameraView?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView?.destroy()
    }
}