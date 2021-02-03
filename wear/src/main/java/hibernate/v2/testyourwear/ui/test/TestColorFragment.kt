package hibernate.v2.testyourwear.ui.test

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import hibernate.v2.testyourwear.databinding.FragmentColorBinding
import hibernate.v2.testyourwear.ui.base.BaseFragment

/**
 * Created by himphen on 21/5/16.
 */
class TestColorFragment : BaseFragment<FragmentColorBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentColorBinding =
        FragmentColorBinding.inflate(inflater, container, false)

    private var i = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding!!.colorView.setOnClickListener {
            changeColor(++i)
        }

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

    private fun changeColor(j: Int) {
        when (j) {
            0 -> viewBinding?.colorView?.setBackgroundColor(Color.RED)
            1 -> viewBinding?.colorView?.setBackgroundColor(Color.GREEN)
            2 -> viewBinding?.colorView?.setBackgroundColor(Color.BLUE)
            3 -> viewBinding?.colorView?.setBackgroundColor(Color.CYAN)
            4 -> viewBinding?.colorView?.setBackgroundColor(Color.MAGENTA)
            5 -> viewBinding?.colorView?.setBackgroundColor(Color.YELLOW)
            6 -> viewBinding?.colorView?.setBackgroundColor(Color.BLACK)
            7 -> viewBinding?.colorView?.setBackgroundColor(Color.WHITE)
            8 -> viewBinding?.colorView?.setBackgroundColor(Color.GRAY)
            9 -> viewBinding?.colorView?.setBackgroundColor(Color.DKGRAY)
            10 -> {
                viewBinding?.colorView?.setBackgroundColor(Color.LTGRAY)
                i = -1
            }
        }
    }
}