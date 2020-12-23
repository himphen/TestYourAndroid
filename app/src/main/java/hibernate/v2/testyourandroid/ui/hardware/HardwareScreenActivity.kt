package hibernate.v2.testyourandroid.ui.hardware

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityColorBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.util.viewBinding

class HardwareScreenActivity : BaseActivity() {

    private val binding by viewBinding(ActivityColorBinding::inflate)
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
        setContentView(R.layout.activity_color)
        init()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        binding.colorView.setOnClickListener {
            if (!testMode) {
                i++
                changeColor(i)
            }
        }
    }

    public override fun onPause() {
        timer.cancel()
        super.onPause()
    }

    private fun changeColor(j: Int) {
        when (j) {
            0 -> binding.colorView.setBackgroundColor(Color.RED)
            1 -> binding.colorView.setBackgroundColor(Color.GREEN)
            2 -> binding.colorView.setBackgroundColor(Color.BLUE)
            3 -> binding.colorView.setBackgroundColor(Color.CYAN)
            4 -> binding.colorView.setBackgroundColor(Color.MAGENTA)
            5 -> binding.colorView.setBackgroundColor(Color.YELLOW)
            6 -> binding.colorView.setBackgroundColor(Color.BLACK)
            7 -> binding.colorView.setBackgroundColor(Color.WHITE)
            8 -> binding.colorView.setBackgroundColor(Color.GRAY)
            9 -> binding.colorView.setBackgroundColor(Color.DKGRAY)
            10 -> {
                binding.colorView.setBackgroundColor(Color.LTGRAY)
                i = -1
            }
        }
    }

    private fun init() {
        openDialogTutor()
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