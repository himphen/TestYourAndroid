package hibernate.v2.testyourandroid.ui.info.monitor

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentMonitorMemoryBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.formatBitSize
import hibernate.v2.testyourandroid.util.viewBinding

class MonitorMemoryFragment : BaseFragment(R.layout.fragment_monitor_memory) {

    private val binding by viewBinding(FragmentMonitorMemoryBinding::bind)
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private var availableValue: Long = 0
    private var usedValue: Long = 0
    private var totalValue: Long = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private val timer: Runnable = object : Runnable {
        override fun run() {
            ramMemory
            try {
                val v = formatBitSize(usedValue, false).toDouble()
                binding.usedText.text = formatBitSize(usedValue, true)
                binding.avaText.text = formatBitSize(availableValue, true)
                series.appendData(DataPoint(lastXValue, v), true, 36)
                binding.graphView.viewport.scrollToEnd()
                lastXValue += 1.0
            } catch (e: Exception) {
                binding.avaText.setText(R.string.ui_not_support)
                binding.usedText.setText(R.string.ui_not_support)
            }
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }
    private var isSupported = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ramMemory
        context?.let { context ->
            binding.avaText.text = formatBitSize(availableValue, true)
            binding.totalText.text = formatBitSize(totalValue, true)
            series.thickness = ConvertUtils.dp2px(4f)
            series.color = ContextCompat.getColor(context, R.color.lineColor3)
            series.isDrawBackground = true
            series.backgroundColor = ContextCompat.getColor(context, R.color.lineColor3A)
            binding.graphView.gridLabelRenderer.gridColor = Color.GRAY
            binding.graphView.gridLabelRenderer.isHighlightZeroLines = false
            binding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
            binding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
            binding.graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            binding.graphView.addSeries(series)
            try {
                isSupported = true
                binding.graphView.viewport.isYAxisBoundsManual = true
                binding.graphView.viewport.setMinY(0.0)
                binding.graphView.viewport.setMaxY(formatBitSize(totalValue, false).toDouble())
                binding.graphView.viewport.isXAxisBoundsManual = true
                binding.graphView.viewport.setMinX(0.0)
                binding.graphView.viewport.setMaxX(36.0)
                binding.graphView.viewport.isScrollable = false
                binding.graphView.viewport.isScalable = false
            } catch (e: NumberFormatException) {
                binding.usedText.setText(R.string.ui_na)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSupported) mHandler.postDelayed(timer, UPDATE_CHART_INTERVAL)
    }

    override fun onPause() {
        super.onPause()
        if (isSupported) mHandler.removeCallbacks(timer)
    }

    private val ramMemory: Unit
        get() {
            context?.let { context ->
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)
                totalValue = memoryInfo.totalMem
                usedValue = memoryInfo.totalMem - memoryInfo.availMem
                availableValue = memoryInfo.availMem
            }
        }

    companion object {
        const val UPDATE_CHART_INTERVAL = 1000L
    }
}