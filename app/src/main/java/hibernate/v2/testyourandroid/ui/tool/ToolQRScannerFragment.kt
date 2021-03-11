package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolQrScannerBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.tool.ToolQRScannerSuccessFragment.Companion.newInstance
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted

/**
 * Created by himphen on 21/5/16.
 */
class ToolQRScannerFragment : BaseFragment<FragmentToolQrScannerBinding>() {

    override val permissions = arrayOf(Manifest.permission.CAMERA)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentToolQrScannerBinding =
        FragmentToolQrScannerBinding.inflate(inflater, container, false)

    private lateinit var mCodeScanner: CodeScanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            mCodeScanner = CodeScanner(context, viewBinding!!.scannerView)
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
            if (!isPermissionsGranted(permissions)) {
                permissionLifecycleObserver?.requestPermissions(permissions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (isPermissionsGranted(permissions)) {
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
        fun newInstance() = ToolQRScannerFragment()
    }
}