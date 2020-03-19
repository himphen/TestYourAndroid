package hibernate.v2.testyourandroid.ui.tool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.RegexUtils
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_tool_qr_scanner_success.*

/**
 * Created by himphen on 21/5/16.
 */
class ToolQRScannerSuccessFragment : BaseFragment() {

    companion object {
        fun newInstance(content: String, barcodeFormatName: String): ToolQRScannerSuccessFragment {
            val fragment = ToolQRScannerSuccessFragment()
            val args = Bundle()
            args.putString("content", content)
            args.putString("barcodeFormatName", barcodeFormatName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tool_qr_scanner_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { arguments ->
            val content = arguments.getString("content")
            val barcodeFormatName = arguments.getString("barcodeFormatName")

            shareButton.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, content)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, resources.getText(R.string.ui_share)))
            }

            urlButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(content)

                if (intent.resolveActivity(it.context.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(it.context, R.string.app_not_found, Toast.LENGTH_LONG).show()
                }
            }

            contentTv.text = content
            formatTv.text = barcodeFormatName

            if (RegexUtils.isURL(content)) {
                urlButton.visibility = View.VISIBLE
            }
        }
    }
}
