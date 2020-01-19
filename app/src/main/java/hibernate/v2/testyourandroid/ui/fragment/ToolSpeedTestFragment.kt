package hibernate.v2.testyourandroid.ui.fragment

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.roundTo
import hibernate.v2.testyourandroid.helper.speedtext.HttpDownloadTest
import hibernate.v2.testyourandroid.helper.speedtext.HttpUploadTest
import hibernate.v2.testyourandroid.helper.speedtext.PingTest
import hibernate.v2.testyourandroid.ui.view.GetSpeedTestHostsHandler
import kotlinx.android.synthetic.main.fragment_tool_speed_test.*
import java.text.DecimalFormat


/**
 * Created by himphen on 21/5/16.
 */
class ToolSpeedTestFragment : BaseFragment() {

    private var seriesPing = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var seriesDownload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var seriesUpload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))

    private var thread: Thread? = null
    private var loadingDialog: MaterialDialog? = null
    private var tempBlackList: HashSet<String> = HashSet()

    private var shouldStopNow = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tool_speed_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            loadingDialog = MaterialDialog(it)
                    .message(text = "Selecting best server based on ping...")
                    .cancelable(false)

            // Init Ping graphic
            graphViewPing.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphViewPing.gridLabelRenderer.isVerticalLabelsVisible = false
            graphViewPing.gridLabelRenderer.gridColor = Color.GRAY
            seriesPing.thickness = 3
            seriesPing.color = ContextCompat.getColor(it, R.color.grey800)
            seriesPing.isDrawBackground = true
            seriesPing.backgroundColor = ContextCompat.getColor(it, R.color.grey800)
            graphViewPing.addSeries(seriesPing)
            graphViewPing.viewport.isXAxisBoundsManual = true
            graphViewPing.viewport.setMinX(0.0)
            graphViewPing.viewport.setMaxX(5.0)

            // Init Download graphic
            graphViewDownload.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphViewDownload.gridLabelRenderer.isVerticalLabelsVisible = false
            graphViewDownload.gridLabelRenderer.gridColor = Color.GRAY
            seriesDownload.thickness = 3
            seriesDownload.color = ContextCompat.getColor(it, R.color.grey800)
            seriesDownload.isDrawBackground = true
            seriesDownload.backgroundColor = ContextCompat.getColor(it, R.color.grey800)
            graphViewDownload.addSeries(seriesDownload)
            graphViewDownload.viewport.isXAxisBoundsManual = true
            graphViewDownload.viewport.setMinX(0.0)
            graphViewDownload.viewport.setMaxX(10.0)

            // Init Upload graphic
            graphViewUpload.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphViewUpload.gridLabelRenderer.isVerticalLabelsVisible = false
            graphViewUpload.gridLabelRenderer.gridColor = Color.GRAY
            seriesUpload.thickness = 3
            seriesUpload.color = ContextCompat.getColor(it, R.color.grey800)
            seriesUpload.isDrawBackground = true
            seriesUpload.backgroundColor = ContextCompat.getColor(it, R.color.grey800)
            graphViewUpload.addSeries(seriesUpload)
            graphViewUpload.viewport.isXAxisBoundsManual = true
            graphViewUpload.viewport.setMinX(0.0)
            graphViewUpload.viewport.setMaxX(10.0)
        }

        startButton.setOnClickListener {
            startButton.isEnabled = false

            serverNameTv.text = ""
            serverDistanceTv.text = ""
            initThread()
            thread?.start()
        }
    }

    private fun initThread() {
        thread = Thread(object : Runnable {
            val dec = DecimalFormat("#.##")
            override fun run() {
                shouldStopNow = false
                HttpUploadTest.shouldStopNow = false
                val getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
                getSpeedTestHostsHandler.start()

                activity?.runOnUiThread {
                    loadingDialog?.show()
                }

                var timeCount = 60
                while (!getSpeedTestHostsHandler.isFinished) {
                    timeCount--
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        activity?.runOnUiThread {
                            startButton?.isEnabled = true
                            loadingDialog?.dismiss()
                        }
                        getSpeedTestHostsHandler.interrupt()
                        return
                    }
                    if (timeCount <= 0) {
                        activity?.runOnUiThread {
                            showErrorDialog("No Connection...")
                            startButton?.isEnabled = true
                            loadingDialog?.dismiss()
                        }
                        getSpeedTestHostsHandler.interrupt()
                        return
                    }
                }

                activity?.runOnUiThread {
                    loadingDialog?.dismiss()
                    seriesPing.resetData(arrayOf(DataPoint(0.0, 0.0)))
                    seriesDownload.resetData(arrayOf(DataPoint(0.0, 0.0)))
                    seriesUpload.resetData(arrayOf(DataPoint(0.0, 0.0)))
                }

                if (getSpeedTestHostsHandler.serverList.isEmpty()) {
                    startButton?.isEnabled = true
                    showErrorDialog("No Connection...")
                    return
                }

                // Find closest server
                var tmp = 19349458.0
                var dist = 0.0
                var findServer: GetSpeedTestHostsHandler.Server = getSpeedTestHostsHandler.serverList[0]

                val source = Location("Source")
                source.latitude = getSpeedTestHostsHandler.selfLat
                source.longitude = getSpeedTestHostsHandler.selfLon

                for (server in getSpeedTestHostsHandler.serverList) {
                    if (tempBlackList.contains(server.sponsor)) {
                        continue
                    }

                    val dest = Location("Dest")
                    dest.latitude = server.lat
                    dest.longitude = server.lon

                    val distance: Double = source.distanceTo(dest).toDouble()
                    if (tmp > distance) {
                        tmp = distance
                        dist = distance
                        findServer = server
                    }
                }

                val uploadAddress = findServer.uploadAddress
                val distance = dist
                activity?.runOnUiThread {
                    serverNameTv.text = String.format("%s @ %s", findServer.sponsor, findServer.name)
                    serverDistanceTv.text = String.format("[Distance: %s km]", DecimalFormat("#.##").format(distance / 1000))
                }

                // Reset value, graphics
                activity?.runOnUiThread {
                    pingTextView?.text = "0 ms"
                    downloadTextView?.text = "0 Mbps"
                    uploadTextView?.text = "0 Mbps"
                }

                var pingTestStarted = false
                var pingTestFinished = false
                var downloadTestStarted = false
                var downloadTestFinished = false
                var uploadTestStarted = false
                var uploadTestFinished = false

                var position: Double
                var lastPositionRotation = 0.0
                var lastXPositionPing = 0.0
                var lastXPositionDownload = 0.0
                var lastXPositionUpload = 0.0

                //Init Test
                val pingTest = PingTest(findServer.host.replace(":8080", ""), 5)
                val downloadTest = HttpDownloadTest(uploadAddress.replace(uploadAddress.split("/").toTypedArray()[uploadAddress.split("/").toTypedArray().size - 1], ""))
                val uploadTest = HttpUploadTest(uploadAddress)
                //Tests
                while (!shouldStopNow) {
                    if (!pingTestStarted) {
                        pingTest.start()
                        pingTestStarted = true
                    }
                    if (pingTestFinished && !downloadTestStarted) {
                        if (pingTest.avgRtt != 0.0) {
                            activity?.runOnUiThread { pingTextView?.text = "${dec.format(pingTest.avgRtt)} ms" }
                        }
                        downloadTest.start()
                        downloadTestStarted = true
                    }
                    if (downloadTestFinished && !uploadTestStarted) {
                        if (downloadTest.finalDownloadRate != 0.0) {
                            activity?.runOnUiThread { downloadTextView?.text = "${dec.format(downloadTest.finalDownloadRate.roundTo(2))} Mbps" }
                        }
                        uploadTest.start()
                        uploadTestStarted = true
                    }

                    // Ping Test
                    if (pingTestStarted && !pingTestFinished) {
                        activity?.runOnUiThread {
                            val pingRate = pingTest.instantRtt
                            pingTextView?.text = "${dec.format(pingRate)} ms"
                            seriesPing.appendData(DataPoint(lastXPositionPing, pingRate), false, 10000)

                            if (graphViewPing.viewport.getMaxX(true) > 5.0) {
                                graphViewPing.viewport.setMaxX(graphViewPing.viewport.getMaxX(true) + 1)
                            }

                            lastXPositionPing++
                        }
                    }

                    //Download Test
                    if (downloadTestStarted && !downloadTestFinished) { //Calc position
                        val downloadRate: Double = downloadTest.instantDownloadRate
                        position = getPositionByRate(downloadRate)
                        activity?.runOnUiThread {
                            val rotate = RotateAnimation(lastPositionRotation.toFloat(), position.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                            rotate.interpolator = LinearInterpolator()
                            rotate.duration = 100
                            barImageView.startAnimation(rotate)
                            downloadTextView.text = "${dec.format(downloadRate)} Mbps"
                        }
                        lastPositionRotation = position
                        //Update chart
                        activity?.runOnUiThread {
                            seriesDownload.appendData(DataPoint(lastXPositionDownload, downloadRate), false, 10000)

                            if (graphViewDownload.viewport.getMaxX(true) > 10.0) {
                                graphViewDownload.viewport.setMaxX(graphViewDownload.viewport.getMaxX(true) + 1)
                            }
                            lastXPositionDownload++
                        }

                    }
                    //Upload Test
                    if (downloadTestFinished && !uploadTestFinished) {
                        // Calc position
                        val uploadRate: Double = uploadTest.instantUploadRate
                        position = getPositionByRate(uploadRate)
                        activity?.runOnUiThread {
                            val rotate = RotateAnimation(lastPositionRotation.toFloat(), position.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                            rotate.interpolator = LinearInterpolator()
                            rotate.duration = 100
                            barImageView.startAnimation(rotate)
                            uploadTextView.text = "${dec.format(uploadRate)} Mbps"
                        }
                        lastPositionRotation = position
                        // Update chart
                        activity?.runOnUiThread {
                            seriesUpload.appendData(DataPoint(lastXPositionUpload, uploadRate), false, 10000)

                            if (graphViewUpload.viewport.getMaxX(true) > 10.0) {
                                graphViewUpload.viewport.setMaxX(graphViewUpload.viewport.getMaxX(true) + 1)
                            }

                            lastXPositionUpload++
                        }
                    }
                    //Test finished
                    if (pingTestFinished && downloadTestFinished && uploadTest.isFinished) {
                        if (uploadTest.getRoundedFinalUploadRate() != 0.0) { //Success
                            activity?.runOnUiThread { uploadTextView?.text = "${dec.format(uploadTest.getRoundedFinalUploadRate())} Mbps" }
                        }
                        break
                    }
                    if (pingTest.isFinished) {
                        pingTestFinished = true
                    }
                    if (downloadTest.isFinished) {
                        downloadTestFinished = true
                    }
                    if (uploadTest.isFinished) {
                        uploadTestFinished = true
                    }
                    if (pingTestStarted && !pingTestFinished) {
                        try {
                            Thread.sleep(300)
                        } catch (e: InterruptedException) {
                        }
                    } else {
                        try {
                            Thread.sleep(100)
                        } catch (e: InterruptedException) {
                        }
                    }
                }

                // Button re-activated at the end of thread
                activity?.runOnUiThread {
                    pingTest.shouldStopNow = true
                    downloadTest.shouldStopNow = true
                    HttpUploadTest.shouldStopNow = true
                    startButton?.isEnabled = true
                }
                return
            }
        })
    }

    private fun stopThread() {
        shouldStopNow = true
    }


    fun getPositionByRate(rate: Double): Double {
        return when {
            rate <= 1 -> (rate * 30)
            rate <= 10 -> ((rate * 6) + 30)
            rate <= 30 -> (((rate - 10) * 3) + 90)
            rate <= 50 -> (((rate - 30) * 1.5) + 150)
            rate <= 100 -> (((rate - 50) * 1.2) + 180)
            rate > 100 -> 240.0
            else -> 0.0
        }
    }

    override fun onPause() {
        super.onPause()
        stopThread()
    }

    private fun showErrorDialog(content: String) {
        context?.let {
            MaterialDialog(it)
                    .title(R.string.ui_error)
                    .message(text = content)
                    .positiveButton(R.string.ui_okay)
                    .cancelable(false)
                    .show()
        }
    }


    companion object {
        fun newInstance(): ToolSpeedTestFragment {
            val fragment = ToolSpeedTestFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}