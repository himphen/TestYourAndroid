package hibernate.v2.testyourandroid.ui.hardware

import android.hardware.Camera
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareCameraBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class HardwareCameraFragment : BaseFragment(R.layout.fragment_hardware_camera) {

    private val binding by viewBinding(FragmentHardwareCameraBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CameraLogger.registerLogger { level, _, _, throwable ->
            if (level == CameraLogger.LEVEL_ERROR) {
                if (throwable != null) {
                    errorNoFeatureDialog(context)
                }
            }
        }

        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)

        binding.cameraView.facing = Facing.BACK
        binding.cameraView.mode = Mode.PICTURE
        binding.cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
        binding.cameraView.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.ZOOM)
        binding.cameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(4, 3), 0f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val numberOfCamera = Camera.getNumberOfCameras()

        if (numberOfCamera > 1) {
            inflater.inflate(R.menu.test_camera, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_flip_camera -> binding.cameraView.toggleFacing()
        }
        return super.onOptionsItemSelected(item)
    }
}