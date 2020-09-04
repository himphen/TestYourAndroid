package hibernate.v2.testyourandroid.ui.info.monitor

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_monitor_cpu.*
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.util.regex.Pattern

class MonitorCPUFragment : BaseFragment(R.layout.fragment_monitor_cpu) {
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private val mHandler = Handler(Looper.getMainLooper())
    private val timer: Runnable = object : Runnable {
        override fun run() {
            speedText.text = "${getCPU(CPU_CURRENT)} MHz"
            series.appendData(DataPoint(lastXValue, getCPU(CPU_CURRENT).toDouble()), true, 36)
            graphView.viewport.scrollToEnd()
            lastXValue += 1.0
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedText.text = "${getCPU(CPU_CURRENT)} MHz"
        coreText.text = numCores.toString()
        minText.text = "${getCPU(CPU_MIN)} MHz"
        maxText.text = "${getCPU(CPU_MAX)} MHz"
        series.thickness = ConvertUtils.dp2px(3f)
        series.color = Color.parseColor("#33B5E5")
        series.isDrawBackground = true
        series.backgroundColor = Color.parseColor("#AA33B5E5")
        graphView.addSeries(series)
        graphView.gridLabelRenderer.gridColor = Color.GRAY
        graphView.gridLabelRenderer.isHighlightZeroLines = false
        graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
        graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinY(0.0)
        graphView.viewport.setMaxY(getCPU(CPU_MAX).toDouble())
        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.setMinX(0.0)
        graphView.viewport.setMaxX(36.0)
        graphView.viewport.isScrollable = false
        graphView.viewport.isScalable = false
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