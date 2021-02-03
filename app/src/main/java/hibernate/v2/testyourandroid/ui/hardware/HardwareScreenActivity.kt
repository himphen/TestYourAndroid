package hibernate.v2.testyourandroid.ui.hardware

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityColorBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity

class HardwareScreenActivity : BaseActivity<ActivityColorBinding>() {

    override fun getActivityViewBinding(): ActivityColorBinding =
        ActivityColorBinding.inflate(layoutInflater)

    private var testMode = false
    private var i = 0
    private val timer: CountDownTimer = object : CountDownTimer(1200000, 100) {
        override fun onFinish() {
            finish()
        }

        override fun onTick(millisUntilFinished: Long) {
            i++
            changeColor(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        viewBinding.colorView.setOnClickListener {
            if (!testMode) {
                i++
                changeColor(i)
            }
        }
        openDialogTutor()
    }

    public override fun onPause() {
        timer.cancel()
        super.onPause()
    }

    private fun changeColor(j: Int) {
        when (j) {
            0 -> viewBinding.colorView.setBackgroundColor(Color.RED)
            1 -> viewBinding.colorView.setBackgroundColor(Color.GREEN)
            2 -> viewBinding.colorView.setBackgroundColor(Color.BLUE)
            3 -> viewBinding.colorView.setBackgroundColor(Color.CYAN)
            4 -> viewBinding.colorView.setBackgroundColor(Color.MAGENTA)
            5 -> viewBinding.colorView.setBackgroundColor(Color.YELLOW)
            6 -> viewBinding.colorView.setBackgroundColor(Color.BLACK)
            7 -> viewBinding.colorView.setBackgroundColor(Color.WHITE)
            8 -> viewBinding.colorView.setBackgroundColor(Color.GRAY)
            9 -> viewBinding.colorView.setBackgroundColor(Color.DKGRAY)
            10 -> {
                viewBinding.colorView.setBackgroundColor(Color.LTGRAY)
                i = -1
            }
        }
    }

    private fun openDialogTestMode() {
        MaterialDialog(this)
            .title(R.string.ui_caution)
            .message(R.string.color_test_message)
            .positiveButton(R.string.ui_okay) {
                testMode = true
                timer.start()
            }
            .negativeButton(R.string.ui_cancel)
            .show()
    }

    private fun openDialogTutor() {
        MaterialDialog(this)
            .title(R.string.color_title)
            .message(R.string.color_message)
            .cancelable(false)
            .positiveButton(R.string.color_test_btn) { openDialogTestMode() }
            .negativeButton(R.string.color_normal_btn)
            .show()
    }
}