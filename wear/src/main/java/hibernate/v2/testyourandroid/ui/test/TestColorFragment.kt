package hibernate.v2.testyourandroid.ui.test

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_color.*

/**
 * Created by himphen on 21/5/16.
 */
class TestColorFragment : BaseFragment() {
    private var i = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun changeColor(j: Int) {
        when (j) {
            0 -> colorView.setBackgroundColor(Color.RED)
            1 -> colorView.setBackgroundColor(Color.GREEN)
            2 -> colorView.setBackgroundColor(Color.BLUE)
            3 -> colorView.setBackgroundColor(Color.CYAN)
            4 -> colorView.setBackgroundColor(Color.MAGENTA)
            5 -> colorView.setBackgroundColor(Color.YELLOW)
            6 -> colorView.setBackgroundColor(Color.BLACK)
            7 -> colorView.setBackgroundColor(Color.WHITE)
            8 -> colorView.setBackgroundColor(Color.GRAY)
            9 -> colorView.setBackgroundColor(Color.DKGRAY)
            10 -> {
                colorView.setBackgroundColor(Color.LTGRAY)
                i = -1
            }
        }
    }

    private fun init() {
        colorView.setOnClickListener {
            i++
            changeColor(i)
        }
    }
}