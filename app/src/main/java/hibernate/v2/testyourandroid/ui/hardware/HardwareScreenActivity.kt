package hibernate.v2.testyourandroid.ui.hardware

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import hibernate.v2.testyourandroid.databinding.ActivityColorBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.util.Utils

class HardwareScreenActivity : BaseActivity<ActivityColorBinding>() {

    override fun getActivityViewBinding(): ActivityColorBinding =
        ActivityColorBinding.inflate(layoutInflater)

    private val colorList = arrayListOf<Any>()

    private var i = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setFullScreen(window)

        viewBinding.colorView.setOnClickListener { changeColor() }

        colorList.addAll(MonitorHelper.getColorList(this))

        changeColor()
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

    override fun onPause() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}