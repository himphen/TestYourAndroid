package hibernate.v2.testyourandroid.ui.tool

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolSoundMeterBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.errorNoFeatureDialog
import hibernate.v2.testyourandroid.util.Utils.logException
import hibernate.v2.testyourandroid.util.ext.convertDpToPx
import hibernate.v2.testyourandroid.util.ext.format
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted
import java.util.ArrayList
import kotlin.math.log10

/**
 * Created by himphen on 21/5/16.
 */
class ToolSoundMeterFragment : BaseFragment<FragmentToolSoundMeterBinding>() {

    override val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentToolSoundMeterBinding =
        FragmentToolSoundMeterBinding.inflate(inflater, container, false)

    private var series = LineGraphSeries(arrayOf<DataPoint>())
    private var lastXValue = 0.0
    private var mindB: Int? = null
    private var maxdB: Int? = null
    private val avgdB = ArrayList<Int>()
    private var mIsRecording = false
    private var mAudioRecord: AudioRecord? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
        }
    }

    override fun onPause() {
        stopRecording()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(permissions)) {
            startRecording()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        context?.let { context ->
            BUFFER_SIZE = AudioRecord.getMinBufferSize(
                SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT
            )
            if (BUFFER_SIZE < 0) {
                errorNoFeatureDialog(context)
                return
            }
            val buffer = ShortArray(BUFFER_SIZE)
            try {
                series.thickness = context.convertDpToPx(3)
                series.color = ContextCompat.getColor(context, R.color.lineColor4)
                series.isDrawBackground = true
                series.backgroundColor = ContextCompat.getColor(context, R.color.lineColor4A)
                viewBinding?.graphView?.addSeries(series)
                viewBinding?.graphView?.viewport?.isYAxisBoundsManual = true
                viewBinding?.graphView?.viewport?.setMinY(0.0)
                viewBinding?.graphView?.viewport?.setMaxY(120.0)
                viewBinding?.graphView?.viewport?.isXAxisBoundsManual = true
                viewBinding?.graphView?.viewport?.setMinX(0.0)
                viewBinding?.graphView?.viewport?.setMaxX(36.0)
                viewBinding?.graphView?.viewport?.isScrollable = false
                viewBinding?.graphView?.viewport?.isScalable = false
                viewBinding?.graphView?.gridLabelRenderer?.gridColor = Color.GRAY
                viewBinding?.graphView?.gridLabelRenderer?.isHighlightZeroLines = false
                viewBinding?.graphView?.gridLabelRenderer?.isHorizontalLabelsVisible = false
                viewBinding?.graphView?.gridLabelRenderer?.padding = context.convertDpToPx(10)
                viewBinding?.graphView?.gridLabelRenderer?.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                mAudioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(
                        SAMPLE_RATE_IN_HZ,
                        AudioFormat.CHANNEL_IN_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                )
                mAudioRecord?.let {
                    it.startRecording()
                    mIsRecording = true
                    viewBinding?.meterCurrentTv?.post(object : Runnable {
                        override fun run() {
                            if (mIsRecording) {
                                val r = it.read(buffer, 0, BUFFER_SIZE)
                                var v: Long = 0
                                for (aBuffer in buffer) {
                                    v += aBuffer * aBuffer.toLong()
                                }
                                val mean = v / r.toDouble()
                                var db = (10 * log10(mean).toFloat()).toInt()
                                if (db < 0) {
                                    db = 0
                                }
                                if (maxdB == null || db > maxdB!!) {
                                    maxdB = db
                                    viewBinding?.meterMaxTv?.text = maxdB.toString()
                                }
                                if (mindB == null || db < mindB!!) {
                                    mindB = db
                                    viewBinding?.meterMinTv?.text = mindB.toString()
                                }
                                avgdB.add(db)
                                viewBinding?.meterAvgTv?.text = avgdB.average().format(0)
                                viewBinding?.meterCurrentTv?.text = db.toString()
                                lastXValue += 0.5
                                series.appendData(DataPoint(lastXValue, db.toDouble()), true, 100)
                                viewBinding?.graphView?.viewport?.scrollToEnd()
                                series.color = when {
                                    db > 100 -> ContextCompat.getColor(context, R.color.lineColor1)
                                    db > 80 -> ContextCompat.getColor(context, R.color.lineColor2)
                                    else -> ContextCompat.getColor(context, R.color.lineColor4)
                                }
                                series.isDrawBackground = true
                                series.backgroundColor = when {
                                    db > 100 -> ContextCompat.getColor(context, R.color.lineColor1A)
                                    db > 80 -> ContextCompat.getColor(context, R.color.lineColor2A)
                                    else -> ContextCompat.getColor(context, R.color.lineColor4A)
                                }
                                viewBinding?.meterCurrentTv?.postDelayed(this, 500)
                            }
                        }
                    })
                }
            } catch (e: Exception) {
                logException(e)
                errorNoFeatureDialog(context)
            }
        }
    }

    private fun stopRecording() {
        mAudioRecord?.let {
            if (mIsRecording) {
                mIsRecording = false
                try {
                    it.stop()
                } catch (ignored: Exception) {
                }
            }
            it.release()
            mAudioRecord = null
        }
    }

    companion object {
        const val SAMPLE_RATE_IN_HZ = 44100
        fun newInstance() = ToolSoundMeterFragment()
        var BUFFER_SIZE = 0
    }
}