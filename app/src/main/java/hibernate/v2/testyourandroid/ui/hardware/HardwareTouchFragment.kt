package hibernate.v2.testyourandroid.ui.hardware

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.view.TestMultiTouchView
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog

/**
 * Created by himphen on 21/5/16.
 */
class HardwareTouchFragment : BaseFragment<ViewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TestMultiTouchView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)?.let {
            if (it) {
                Toast.makeText(context, R.string.touch_message, Toast.LENGTH_LONG).show()
            } else {
                errorNoFeatureDialog(context)
            }
        }
    }
}