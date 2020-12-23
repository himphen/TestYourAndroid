package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolQrScannerBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.tool.ToolQRScannerSuccessFragment.Companion.newInstance
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
class ToolQRScannerFragment : BaseFragment(R.layout.fragment_tool_qr_scanner) {

    private val binding by viewBinding(FragmentToolQrScannerBinding::bind)

    private lateinit var mCodeScanner: CodeScanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            mCodeScanner = CodeScanner(context, binding.scannerView)
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
                requestMultiplePermissions.launch(PERMISSION_NAME)
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

    companion object {
        private val PERMISSION_NAME = arrayOf(Manifest.permission.CAMERA)

        fun newInstance(): ToolQRScannerFragment {
            return ToolQRScannerFragment()
        }
    }
}