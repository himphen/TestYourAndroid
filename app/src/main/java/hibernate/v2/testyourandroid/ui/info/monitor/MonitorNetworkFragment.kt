package hibernate.v2.testyourandroid.ui.info.monitor

import android.graphics.Color
import android.net.TrafficStats
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
import hibernate.v2.testyourandroid.databinding.FragmentMonitorNetworkBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.formatSpeedSize
import hibernate.v2.testyourandroid.util.viewBinding

class MonitorNetworkFragment : BaseFragment(R.layout.fragment_monitor_network) {

    private val binding by viewBinding(FragmentMonitorNetworkBinding::bind)
    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var series2 = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private val mHandler = Handler(Looper.getMainLooper())
    private var differenceRx: Long = 0
    private var differenceTx: Long = 0
    private var lastTotalRx: Long = 0
    private var lastTotalTx: Long = 0

    private val timer: Runnable = object : Runnable {
        override fun run() {
            networkUsage
            try {
                val tx = formatSpeedSize(differenceTx, false).toDouble()
                val rx = formatSpeedSize(differenceRx, false).toDouble()
                binding.upSpeedText.text = formatSpeedSize(differenceTx, true)
                binding.downSpeedText.text = formatSpeedSize(differenceRx, true)
                series.appendData(DataPoint(lastXValue, rx), true, 36)
                series2.appendData(DataPoint(lastXValue, tx), true, 36)
                binding.graphView.viewport.scrollToEnd()
                lastXValue += 1.0
            } catch (e: Exception) {
                binding.upSpeedText.setText(R.string.ui_not_support)
                binding.downSpeedText.setText(R.string.ui_not_support)
            }
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }
    private var isSupported = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastTotalRx = TrafficStats.getTotalRxBytes()
        lastTotalTx = TrafficStats.getTotalTxBytes()
        if (lastTotalRx == TrafficStats.UNSUPPORTED.toLong() || lastTotalTx == TrafficStats.UNSUPPORTED.toLong()) {
            binding.upSpeedText.setText(R.string.ui_not_support)
            binding.downSpeedText.setText(R.string.ui_not_support)
        } else {
            context?.let { context ->
                isSupported = true
                series.thickness = ConvertUtils.dp2px(4f)
                series.color = ContextCompat.getColor(context, R.color.lineColor4)
                series.isDrawBackground = true
                series.color = ContextCompat.getColor(context, R.color.lineColor4A)
                series2.thickness = ConvertUtils.dp2px(4f)
                series2.color = ContextCompat.getColor(context, R.color.lineColor2)
                series2.isDrawBackground = true
                series2.color = ContextCompat.getColor(context, R.color.lineColor2A)
                binding.graphView.addSeries(series)
                binding.graphView.addSeries(series2)
                binding.graphView.gridLabelRenderer.gridColor = Color.GRAY
                binding.graphView.gridLabelRenderer.isHighlightZeroLines = false
                binding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
                binding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
                binding.graphView.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                binding.graphView.viewport.isXAxisBoundsManual = true
                binding.graphView.viewport.setMinX(0.0)
                binding.graphView.viewport.setMaxX(36.0)
                binding.graphView.viewport.isScrollable = false
                binding.graphView.viewport.isScalable = false
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

    private val networkUsage: Unit
        get() {
            differenceRx = TrafficStats.getTotalRxBytes() - lastTotalRx
            lastTotalRx = TrafficStats.getTotalRxBytes()
            differenceTx = TrafficStats.getTotalTxBytes() - lastTotalTx
            lastTotalTx = TrafficStats.getTotalTxBytes()
        }

    companion object {
        const val UPDATE_CHART_INTERVAL = 1000L
    }
}