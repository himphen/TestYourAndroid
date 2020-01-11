package hibernate.v2.testyourandroid.ui.fragment

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.formatBitSize
import kotlinx.android.synthetic.main.fragment_monitor_memory.*

class InfoSystemMonitorMemoryFragment : BaseFragment() {
    private var series = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var lastXValue = 0.0
    private var availableValue: Long = 0
    private var usedValue: Long = 0
    private var totalValue: Long = 0
    private val mHandler = Handler()
    private val timer: Runnable = object : Runnable {
        override fun run() {
            ramMemory
            lastXValue += 1.0
            try {
                val v = formatBitSize(usedValue, false).toDouble()
                usedText.text = formatBitSize(usedValue, true)
                avaText.text = formatBitSize(availableValue, true)
                series.appendData(DataPoint(lastXValue, v), true, 100)
            } catch (e: Exception) {
                avaText.setText(R.string.ui_not_support)
                usedText.setText(R.string.ui_not_support)
            }
            mHandler.postDelayed(this, 1000)
        }
    }
    private var isSupported = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_monitor_memory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ramMemory
        avaText.text = formatBitSize(availableValue, true)
        totalText.text = formatBitSize(totalValue, true)
        series.thickness = ConvertUtils.dp2px(3f)
        series.color = Color.parseColor("#FF8800")
        series.isDrawBackground = true
        series.backgroundColor = Color.parseColor("#AAFF8800")
        graphView.gridLabelRenderer.gridColor = Color.GRAY
        graphView.gridLabelRenderer.isHighlightZeroLines = false
        graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
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

    override fun onResume() {
        super.onResume()
        if (isSupported) mHandler.postDelayed(timer, 1000)
    }

    override fun onPause() {
        super.onPause()
        if (isSupported) mHandler.removeCallbacks(timer)
    }

    private val ramMemory: Unit
        get() {
            context?.let { context ->
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)
                totalValue = memoryInfo.totalMem
                usedValue = memoryInfo.totalMem - memoryInfo.availMem
                availableValue = memoryInfo.availMem
            }
        }
}