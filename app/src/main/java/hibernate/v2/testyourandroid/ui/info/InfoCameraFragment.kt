package hibernate.v2.testyourandroid.ui.info

import android.Manifest
import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.errorNoFeatureDialog
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
@Suppress("DEPRECATION")
class InfoCameraFragment : BaseFragment() {
    private var mCamera: Camera? = null

    private var cameraId = 0
    private var mParameters: Camera.Parameters? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(PERMISSION_NAME)) {
            if (mCamera == null) {
                openChooseCameraDialog()
            } else {
                initCamera(cameraId)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mCamera != null) {
            mCamera!!.release()
        }
    }

    private fun openChooseCameraDialog() {
        val numberOfCamera = Camera.getNumberOfCameras()
        if (numberOfCamera == 1) {
            initCamera(0)
        } else {
            context?.let {
                MaterialDialog(it)
                        .title(R.string.dialog_camera_title)
                        .listItemsSingleChoice(
                                items = arrayListOf("Camera 1", "Camera 2"),
                                waitForPositiveButton = false
                        ) { dialog, index, _ ->
                            initCamera(index)
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

    private fun initCamera(which: Int) {
        cameraId = which
        try {
            mCamera = Camera.open(which)
            mParameters = mCamera?.parameters
        } catch (e: Exception) {
            e.printStackTrace()
            errorNoFeatureDialog(context)
            return
        }
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_camera_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        rvlist!!.adapter = adapter
    }

    private fun integerListToString(list: List<Int>?): String {
        if (list == null) return "Not supported"
        val tempList = StringBuilder()

        for (element in list) {
            val format = when (element) {
                256 -> "JPEG"
                16 -> "NV16"
                17 -> "NV21"
                4 -> "RGB_565"
                20 -> "YUY2"
                842094169 -> "YV12"
                0 -> "UNKNOWN"
                else -> "UNKNOWN"
            }
            tempList.append(format).append("\n")
        }
        return tempList.substring(0, tempList.length - 1)
    }

    private fun listToString(list: List<String>?): String {
        if (list == null) return "Not supported"
        val tempList = StringBuilder()
        for (element in list) tempList.append(element).append("\n")
        return tempList.substring(0, tempList.length - 1)
    }

    private fun sizeListToString(list: List<Camera.Size>): String {
        val tempList = StringBuilder()
        for (element in list) tempList.append(element.width).append(" X ").append(element.height).append("\n")
        return tempList.substring(0, tempList.length - 1)
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0, 1 -> {
                    val displayMetrics = DisplayMetrics()
                    val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                    val screenWidth = displayMetrics.widthPixels
                    val screenHeight = displayMetrics.heightPixels
                    if (j == 0) "$screenWidth px" else "$screenHeight px"
                }
                2 -> {
                    mParameters?.let {
                        sizeListToString(it.supportedPictureSizes)
                    } ?: run {
                        ""
                    }
                }
                3 -> {
                    listToString(mParameters?.supportedAntibanding)
                }
                4 -> {
                    listToString(mParameters?.supportedColorEffects)
                }
                5 -> {
                    listToString(mParameters?.supportedFlashModes)
                }
                6 -> {
                    listToString(mParameters?.supportedFocusModes)
                }
                7 -> {
                    listToString(mParameters?.supportedWhiteBalance)
                }
                8 -> {
                    listToString(mParameters?.supportedSceneModes)
                }
                9 -> {
                    integerListToString(mParameters?.supportedPictureFormats)
                }
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
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