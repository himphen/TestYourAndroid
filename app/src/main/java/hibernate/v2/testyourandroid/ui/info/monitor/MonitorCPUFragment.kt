package hibernate.v2.testyourandroid.ui.info.monitor

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentMonitorCpuBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.util.regex.Pattern

class MonitorCPUFragment : BaseFragment<FragmentMonitorCpuBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMonitorCpuBinding =
        FragmentMonitorCpuBinding.inflate(inflater, container, false)

    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private val mHandler = Handler(Looper.getMainLooper())
    private val timer: Runnable = object : Runnable {
        override fun run() {
            viewBinding?.speedText?.text = "${getCPU(CPU_CURRENT)} MHz"
            series.appendData(DataPoint(lastXValue, getCPU(CPU_CURRENT).toDouble()), true, 36)
            viewBinding?.graphView?.viewport?.scrollToEnd()
            lastXValue += 1.0
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.let { viewBinding ->
            context?.let { context ->
                viewBinding.speedText.text = "${getCPU(CPU_CURRENT)} MHz"
                viewBinding.coreText.text = numCores.toString()
                viewBinding.minText.text = "${getCPU(CPU_MIN)} MHz"
                viewBinding.maxText.text = "${getCPU(CPU_MAX)} MHz"
                series.thickness = ConvertUtils.dp2px(4f)
                series.color = ContextCompat.getColor(context, R.color.lineColor1)
                series.isDrawBackground = true
                series.backgroundColor = ContextCompat.getColor(context, R.color.lineColor1A)
                viewBinding.graphView.addSeries(series)
                viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
                viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
                viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
                viewBinding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
                viewBinding.graphView.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                viewBinding.graphView.viewport.isYAxisBoundsManual = true
                viewBinding.graphView.viewport.setMinY(0.0)
                viewBinding.graphView.viewport.setMaxY(getCPU(CPU_MAX).toDouble())
                viewBinding.graphView.viewport.isXAxisBoundsManual = true
                viewBinding.graphView.viewport.setMinX(0.0)
                viewBinding.graphView.viewport.setMaxX(36.0)
                viewBinding.graphView.viewport.isScrollable = false
                viewBinding.graphView.viewport.isScalable = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler.postDelayed(timer, UPDATE_CHART_INTERVAL)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(timer)
    }

    private fun getCPU(type: Int): Int {
        var filename = ""
        when (type) {
            CPU_MIN -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
            CPU_MAX -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            CPU_CURRENT -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
        }
        val list = ArrayList<String>()
        val file = File(filename)
        if (file.exists()) {
            try {
                val bufferedReader = BufferedReader(FileReader(file))
                for (line in bufferedReader.readLine()) {
                    list.add(line.toString())
                }
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val sd = StringBuilder()
        for (e in list) {
            sd.append(e)
        }
        return try {
            Integer.valueOf(sd.toString()) / 1000
        } catch (e: NumberFormatException) {
            0
        }
    }

    // Default to return 1 core
    // Get directory containing CPU info
    // Filter to only list the devices we care about
    // Return the number of cores (virtual CPU devices)
    // Check if filename is "cpu", followed by a single digit number
    // Private Class to display only CPU devices in the directory listing
    private val numCores: Int
        get() { // Private Class to display only CPU devices in the directory listing
            class CpuFilter : FileFilter {
                override fun accept(pathname: File): Boolean { // Check if filename is "cpu", followed by a single digit number
                    return Pattern.matches("cpu[0-9]", pathname.name)
                }
            }
            return try { // Get directory containing CPU info
                val dir = File("/sys/devices/system/cpu/")
                // Filter to only list the devices we care about
                val files = dir.listFiles(CpuFilter())
                // Return the number of cores (virtual CPU devices)
                files?.size ?: 1
            } catch (e: Exception) { // Default to return 1 core
                1
            }
        }

    companion object {
        private const val CPU_MIN = 0
        private const val CPU_MAX = 1
        private const val CPU_CURRENT = 2
        const val UPDATE_CHART_INTERVAL = 1000L
    }
}