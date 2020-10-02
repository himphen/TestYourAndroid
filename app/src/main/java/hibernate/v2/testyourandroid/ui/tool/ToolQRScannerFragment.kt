package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.tool.ToolQRScannerSuccessFragment.Companion.newInstance
import kotlinx.android.synthetic.main.fragment_tool_qr_scanner.*

/**
 * Created by himphen on 21/5/16.
 */
class ToolQRScannerFragment : BaseFragment(R.layout.fragment_tool_qr_scanner) {

    private lateinit var mCodeScanner: CodeScanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            mCodeScanner = CodeScanner(context, scannerView)
            mCodeScanner.setErrorCallback {
                Handler(Looper.getMainLooper()).post {
                    Utils.errorNoFeatureDialog(context)
                }
            }
            mCodeScanner.decodeCallback = DecodeCallback { result ->
                activity?.runOnUiThread {
                    activity?.let {
                        val manager = it.supportFragmentManager
                        val transaction = manager.beginTransaction()
                        val fragment = newInstance(
                            result.text,
                            result.barcodeFormat.name
                        )
                        transaction.replace(R.id.container, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }
            }
            if (!isPermissionsGranted(PERMISSION_NAME)) {
                requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (isPermissionsGranted(PERMISSION_NAME)) {
                mCodeScanner.startPreview()
            }
        } catch (e: Exception) {
            Utils.errorNoFeatureDialog(context)
        }
    }

    override fun onPause() {
        mCodeScanner.releaseResources()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                Utils.openErrorPermissionDialog(context)
            }
        }
    }

    companion object {
        private val PERMISSION_NAME = arrayOf(Manifest.permission.CAMERA)

        fun newInstance(): ToolQRScannerFragment {
            return ToolQRScannerFragment()
        }
    }
}