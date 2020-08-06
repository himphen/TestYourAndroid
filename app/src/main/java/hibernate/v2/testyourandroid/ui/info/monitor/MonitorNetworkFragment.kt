package hibernate.v2.testyourandroid.ui.info.monitor

import android.graphics.Color
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ConvertUtils
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.formatSpeedSize
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_monitor_network.*

class MonitorNetworkFragment : BaseFragment() {
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
                upSpeedText.text = formatSpeedSize(differenceTx, true)
                downSpeedText.text = formatSpeedSize(differenceRx, true)
                series.appendData(DataPoint(lastXValue, rx), true, 36)
                series2.appendData(DataPoint(lastXValue, tx), true, 36)
                graphView.viewport.scrollToEnd()
                lastXValue += 1.0
            } catch (e: Exception) {
                upSpeedText.setText(R.string.ui_not_support)
                downSpeedText.setText(R.string.ui_not_support)
            }
            mHandler.postDelayed(this, UPDATE_CHART_INTERVAL)
        }
    }
    private var isSupported = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monitor_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastTotalRx = TrafficStats.getTotalRxBytes()
        lastTotalTx = TrafficStats.getTotalTxBytes()
        if (lastTotalRx == TrafficStats.UNSUPPORTED.toLong() || lastTotalTx == TrafficStats.UNSUPPORTED.toLong()) {
            upSpeedText.setText(R.string.ui_not_support)
            downSpeedText.setText(R.string.ui_not_support)
        } else {
            isSupported = true
            series.thickness = ConvertUtils.dp2px(3f)
            series.color = Color.parseColor("#AA66CC")
            series.isDrawBackground = true
            series.backgroundColor = Color.parseColor("#AAAA66CC")
            series2.thickness = ConvertUtils.dp2px(3f)
            series2.color = Color.parseColor("#99CC00")
            series2.isDrawBackground = true
            series2.backgroundColor = Color.parseColor("#AA99CC00")
            graphView.addSeries(series)
            graphView.addSeries(series2)
            graphView.gridLabelRenderer.gridColor = Color.GRAY
            graphView.gridLabelRenderer.isHighlightZeroLines = false
            graphView.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphView.gridLabelRenderer.padding = ConvertUtils.dp2px(10f)
            graphView.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graphView.viewport.isXAxisBoundsManual = true
            graphView.viewport.setMinX(0.0)
            graphView.viewport.setMaxX(36.0)
            graphView.viewport.isScrollable = false
            graphView.viewport.isScalable = false
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