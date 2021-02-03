package hibernate.v2.testyourandroid.ui.info.monitor

import android.graphics.Color
import android.net.TrafficStats
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
import hibernate.v2.testyourandroid.databinding.FragmentMonitorNetworkBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.formatSpeedSize

class MonitorNetworkFragment : BaseFragment<FragmentMonitorNetworkBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMonitorNetworkBinding =
        FragmentMonitorNetworkBinding.inflate(inflater, container, false)

    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var series2 = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private val mHandler = Handler(Looper.getMainLooper())
    private var differenceRx: Long = 0
    private var differenceTx: Long = 0
    private var lastTotalRx: Long = 0
    private var lastTotalTx: Long = 0

    private val timer: Runnable by lazy {
        object : Runnable {
            override fun run() {
                networkUsage
                try {
                    val tx = formatSpeedSize(differenceTx, false).toDouble()
                    val rx = formatSpeedSize(differenceRx, false).toDouble()
                    viewBinding?.upSpeedText?.text = formatSpeedSize(differenceTx, true)
                    viewBinding?.downSpeedText?.text = formatSpeedSize(differenceRx, true)
                    series.appendData(DataPoint(lastXValue, rx), true, 36)
                    series2.appendData(DataPoint(lastXValue, tx), true, 36)
                    viewBinding?.graphView?.viewport?.scrollToEnd()
                    lastXValue += 1.0
                } catch (e: Exception) {
                    viewBinding?.upSpeedText?.setText(R.string.ui_not_support)
                    viewBinding?.downSpeedText?.setText(R.string.ui_not_support)
                }
                mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
            }
        }
    }
    private var isSupported = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastTotalRx = TrafficStats.getTotalRxBytes()
        lastTotalTx = TrafficStats.getTotalTxBytes()

        viewBinding?.let { viewBinding ->
            if (lastTotalRx == TrafficStats.UNSUPPORTED.toLong() || lastTotalTx == TrafficStats.UNSUPPORTED.toLong()) {
                viewBinding.upSpeedText.setText(R.string.ui_not_support)
                viewBinding.downSpeedText.setText(R.string.ui_not_support)
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
                    viewBinding.graphView.addSeries(series)
                    viewBinding.graphView.addSeries(series2)
                    viewBinding.graphView.gridLabelRenderer.gridColor = Color.GRAY
                    viewBinding.graphView.gridLabelRenderer.isHighlightZeroLines = false
                    viewBinding.graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
                    viewBinding.graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
                    viewBinding.graphView.gridLabelRenderer.gridStyle =
                        GridLabelRenderer.GridStyle.HORIZONTAL
                    viewBinding.graphView.viewport.isXAxisBoundsManual = true
                    viewBinding.graphView.viewport.setMinX(0.0)
                    viewBinding.graphView.viewport.setMaxX(36.0)
                    viewBinding.graphView.viewport.isScrollable = false
                    viewBinding.graphView.viewport.isScalable = false
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