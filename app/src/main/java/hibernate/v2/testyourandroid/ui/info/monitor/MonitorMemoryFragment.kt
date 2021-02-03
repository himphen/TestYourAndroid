package hibernate.v2.testyourandroid.ui.info.monitor

import android.app.ActivityManager
import android.content.Context
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
import hibernate.v2.testyourandroid.databinding.FragmentMonitorMemoryBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.formatBitSize

class MonitorMemoryFragment : BaseFragment<FragmentMonitorMemoryBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMonitorMemoryBinding =
        FragmentMonitorMemoryBinding.inflate(inflater, container, false)

    private lateinit var activityManager: ActivityManager
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private var availableValue: Long = 0
    private var usedValue: Long = 0
    private var totalValue: Long = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private val timer: Runnable by lazy {
        object : Runnable {
            override fun run() {
                getRamMemory()
                try {
                    val v = formatBitSize(usedValue, false).toDouble()
                    viewBinding?.usedText?.text = formatBitSize(usedValue, true)
                    viewBinding?.avaText?.text = formatBitSize(availableValue, true)
                    series.appendData(DataPoint(lastXValue, v), true, 36)
                    viewBinding?.graphView?.viewport?.scrollToEnd()
                    lastXValue += 1.0
                } catch (e: Exception) {
                    viewBinding?.avaText?.setText(R.string.ui_not_support)
                    viewBinding?.usedText?.setText(R.string.ui_not_support)
                }
                mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
            }
        }
    }
    private var isSupported = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.let { viewBinding ->
            context?.let { context ->
                activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                getRamMemory()

                viewBinding.avaText.text = formatBitSize(availableValue, true)
                viewBinding.totalText.text = formatBitSize(totalValue, true)
                series.thickness = ConvertUtils.dp2px(4f)
                series.color = ContextCompat.getColor(context, R.color.lineColor3)
                series.isDrawBackground = true
                series.backgroundColor = ContextCompat.getColor(context, R.color.lineColor3A)
                viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
                viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
                viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
                viewBinding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
                viewBinding.graphView.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                viewBinding.graphView.addSeries(series)
                try {
                    isSupported = true
                    viewBinding.graphView.viewport.isYAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinY(0.0)
                    viewBinding.graphView.viewport.setMaxY(
                        formatBitSize(
                            totalValue,
                            false
                        ).toDouble()
                    )
                    viewBinding.graphView.viewport.isXAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinX(0.0)
                    viewBinding.graphView.viewport.setMaxX(36.0)
                    viewBinding.graphView.viewport.isScrollable = false
                    viewBinding.graphView.viewport.isScalable = false
                } catch (e: NumberFormatException) {
                    viewBinding.usedText.setText(R.string.ui_na)
                }
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

    private fun getRamMemory() {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        totalValue = memoryInfo.totalMem
        usedValue = memoryInfo.totalMem - memoryInfo.availMem
        availableValue = memoryInfo.availMem
    }

    companion object {
        const val UPDATE_CHART_INTERVAL = 1000L
    }
}