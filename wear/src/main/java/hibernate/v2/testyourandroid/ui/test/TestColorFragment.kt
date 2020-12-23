package hibernate.v2.testyourandroid.ui.test

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentColorBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.viewBinding

/**
 * Created by himphen on 21/5/16.
 */
class TestColorFragment : BaseFragment(R.layout.fragment_color) {

    private val binding by viewBinding(FragmentColorBinding::bind)
    private var i = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
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
        binding.colorView.setOnClickListener {
            i++
            changeColor(i)
        }
    }
}