package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.View
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareFlashlightBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class ToolFlashlightFragment : BaseFragment(R.layout.fragment_hardware_flashlight) {

    private val binding by viewBinding(FragmentHardwareFlashlightBinding::bind)

    private var mCamera: Camera? = null
    private var isFlashlightOn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.turnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                openFlash()
            } else {
                closeFlash()
            }
        }

        if (arguments?.getBoolean(ARG_AUTO_OPEN, false) == true) {
            binding.turnSwitch.toggle()
        }
    }

    override fun onDestroyView() {
        closeFlash()
        super.onDestroyView()
    }

    private fun openFlash() {
        if (isFlashlightOn) return

        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)?.let {
            if (it) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (isPermissionsGranted(PERMISSION_NAME)) {
                            try {
                                val cameraManager =
                                    context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                                // Usually front camera is at 0 position.
                                cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                                isFlashlightOn = true
                                return
                            } catch (e: Exception) {
                            }
                        } else {
                            requestMultiplePermissions.launch(PERMISSION_NAME)
                            binding.turnSwitch.isChecked = false
                            isFlashlightOn = false
                            return
                        }
                    } else {
                        mCamera = Camera.open(0)
                        mCamera?.let { mCamera ->
                            val mParams = mCamera.parameters
                            mCamera.parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                            mCamera.parameters = mParams
                            mCamera.startPreview()
                        }
                        return
                    }
                } catch (e: Exception) {
                }
            }
        }

        errorNoFeatureDialog(context)
        binding.turnSwitch.isChecked = false
        isFlashlightOn = false
    }

    private fun closeFlash() {
        if (!isFlashlightOn) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val cameraManager =
                    context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

                val cameraId =
                    cameraManager.cameraIdList[0] // Usually front camera is at 0 position.
                cameraManager.setTorchMode(cameraId, false)
            } else {
                mCamera?.let { mCamera ->
                    val mParams = mCamera.parameters
                    mParams?.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    mCamera.parameters = mParams
                    mCamera.stopPreview()
                    mCamera.release()
                }
                mCamera = null
            }
        } catch (ignored: Exception) {
        }
        binding.turnSwitch.isChecked = false
        isFlashlightOn = false
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.CAMERA)

        private const val ARG_AUTO_OPEN = "auto_open"
        fun newInstance(autoOpen: Boolean): ToolFlashlightFragment {
            val fragment = ToolFlashlightFragment()
            val args = Bundle()
            args.putBoolean(ARG_AUTO_OPEN, autoOpen)
            fragment.arguments = args
            return fragment
        }
    }
}