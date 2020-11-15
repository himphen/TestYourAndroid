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
import hibernate.v2.testyourandroid.util.Utils.formatBitSize
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_monitor_memory.*

class MonitorMemoryFragment : BaseFragment(R.layout.fragment_monitor_memory) {
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
                usedText.text = formatBitSize(usedValue, true)
                avaText.text = formatBitSize(availableValue, true)
                series.appendData(DataPoint(lastXValue, v), true, 36)
                graphView.viewport.scrollToEnd()
                lastXValue += 1.0
            } catch (e: Exception) {
                avaText.setText(R.string.ui_not_support)
                usedText.setText(R.string.ui_not_support)
            }
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }
    private var isSupported = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ramMemory
        context?.let { context->
            avaText.text = formatBitSize(availableValue, true)
            totalText.text = formatBitSize(totalValue, true)
            series.thickness = ConvertUtils.dp2px(4f)
            series.color = ContextCompat.getColor(context, R.color.lineColor3)
            series.isDrawBackground = true
            series.backgroundColor = ContextCompat.getColor(context, R.color.lineColor3A)
            graphView.gridLabelRenderer.gridColor = Color.GRAY
            graphView.gridLabelRenderer.isHighlightZeroLines = false
            graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
            graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graphView.addSeries(series)
            try {
                isSupported = true
                graphView.viewport.isYAxisBoundsManual = true
                graphView.viewport.setMinY(0.0)
                graphView.viewport.setMaxY(formatBitSize(totalValue, false).toDouble())
                graphView.viewport.isXAxisBoundsManual = true
                graphView.viewport.setMinX(0.0)
                graphView.viewport.setMaxX(36.0)
                graphView.viewport.isScrollable = false
                graphView.viewport.isScalable = false
            } catch (e: NumberFormatException) {
                usedText.setText(R.string.ui_na)
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