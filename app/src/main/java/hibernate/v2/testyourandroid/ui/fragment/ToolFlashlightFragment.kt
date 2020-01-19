package hibernate.v2.testyourandroid.ui.fragment

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
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import kotlinx.android.synthetic.main.fragment_hardware_flashlight.*

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class ToolFlashlightFragment : BaseFragment() {

    private var mCamera: Camera? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hardware_flashlight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        turnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                openFlash()
            } else {
                closeFlash()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeFlash()
    }

    private fun openFlash() {
        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)?.let {
            if (it) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (isPermissionsGranted(PERMISSION_NAME)) {
                            val cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            try {
                                // Usually front camera is at 0 position.
                                cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)

                                return
                            } catch (e: Exception) {
                            }
                        } else {
                            requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
                            turnSwitch.isChecked = false

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
        turnSwitch.isChecked = false
    }

    private fun closeFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

                val cameraId = cameraManager.cameraIdList[0] // Usually front camera is at 0 position.
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
        turnSwitch.isChecked = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                openErrorPermissionDialog(context)
            }
        }
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.CAMERA)
    }
}