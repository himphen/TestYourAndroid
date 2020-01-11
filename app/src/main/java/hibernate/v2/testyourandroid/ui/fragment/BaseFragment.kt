package hibernate.v2.testyourandroid.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.isPermissionsGranted

abstract class BaseFragment : Fragment() {

    protected fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return isPermissionsGranted(context, permissions)
    }

    protected fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        return UtilHelper.hasAllPermissionsGranted(grantResults)
    }

    protected fun showSnackbar(view: View, stringRid: Int): Snackbar {
        val snackbar = Snackbar.make(view, stringRid, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundResource(R.color.primary_dark)
        (sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackbar.show()
        return snackbar
    }

    protected fun showSnackbar(view: View, string: String): Snackbar {
        val snackbar = Snackbar.make(view, string, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundResource(R.color.primary_dark)
        (sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackbar.show()
        return snackbar
    }

    protected fun setBlueSnackbar(snackbar: Snackbar): Snackbar {
        val sbView = snackbar.view
        sbView.setBackgroundResource(R.color.primary_dark)
        (sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        context?.let { context ->
            (sbView.findViewById<View>(com.google.android.material.R.id.snackbar_action) as TextView).setTextColor(
                    ContextCompat.getColor(context, R.color.gold)
            )
        }
        return snackbar
    }

    companion object {
        const val DELAY_AD_LAYOUT = 200
        const val PERMISSION_REQUEST_CODE = 100
    }
}