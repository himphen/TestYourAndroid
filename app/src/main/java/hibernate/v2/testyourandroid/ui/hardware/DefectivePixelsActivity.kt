package hibernate.v2.testyourandroid.ui.hardware

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityColorBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.util.Utils

class DefectivePixelsActivity : BaseActivity<ActivityColorBinding>() {

    override fun getActivityViewBinding(): ActivityColorBinding =
        ActivityColorBinding.inflate(layoutInflater)

    private val colorList = arrayListOf<Any>()

    private var i = 0
    private val timer: CountDownTimer = object : CountDownTimer(1200000, 100) {
        override fun onFinish() = finish()
        override fun onTick(millisUntilFinished: Long) = changeColor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setFullScreen(window)

        viewBinding.colorView.setOnClickListener { finish() }

        colorList.addAll(MonitorHelper.getColorList(this))
    }

    override fun onPause() {
        timer.cancel()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        openDialogTestMode()
    }

    private fun changeColor() {
        i++
        var selected = colorList.getOrNull(i)
        if (selected == null) {
            i = 0
            selected = colorList[i]
        }

        if (selected is MColor) {
            viewBinding.colorView.setBackgroundColor(ContextCompat.getColor(this, selected.colorId))
        } else if (selected is GradientDrawable) {
            viewBinding.colorView.background = selected
        }
    }

    private fun openDialogTestMode() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.ui_caution)
            .setMessage(R.string.color_test_message)
            .setCancelable(false)
            .setPositiveButton(R.string.ui_okay) { dialog, which ->
                timer.start()
            }
            .setNegativeButton(R.string.ui_cancel) { dialog, which ->
                finish()
            }
            .show()
    }
}