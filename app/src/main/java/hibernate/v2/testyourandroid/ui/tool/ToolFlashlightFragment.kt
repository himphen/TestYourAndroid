package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.databinding.FragmentHardwareFlashlightBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class ToolFlashlightFragment : BaseFragment<FragmentHardwareFlashlightBinding>() {

    override val permissions = arrayOf(Manifest.permission.CAMERA)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHardwareFlashlightBinding =
        FragmentHardwareFlashlightBinding.inflate(inflater, container, false)

    private var mCamera: Camera? = null
    private var isFlashlightOn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.turnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                openFlash()
            } else {
                closeFlash()
            }
        }

        if (arguments?.getBoolean(ARG_AUTO_OPEN, false) == true) {
            viewBinding!!.turnSwitch.toggle()
        }
    }

    override fun onStop() {
        closeFlash()
        super.onStop()
    }

    private fun openFlash() {
        if (isFlashlightOn) return

        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)?.let {
            if (it) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (isPermissionsGranted(permissions)) {
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
                            permissionLifecycleObserver?.requestPermissions(permissions)
                            viewBinding?.turnSwitch?.isChecked = false
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
        viewBinding?.turnSwitch?.isChecked = false
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
        viewBinding?.turnSwitch?.isChecked = false
        isFlashlightOn = false
    }

    companion object {
        private const val ARG_AUTO_OPEN = "auto_open"
        fun newInstance(autoOpen: Boolean) =
            ToolFlashlightFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_AUTO_OPEN, autoOpen)
                }
            }
    }
}