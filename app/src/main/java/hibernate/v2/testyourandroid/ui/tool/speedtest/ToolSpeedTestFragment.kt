package hibernate.v2.testyourandroid.ui.tool.speedtest

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.roundTo
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.view.GetSpeedTestHostsHandler
import kotlinx.android.synthetic.main.fragment_tool_speed_test.*
import java.text.DecimalFormat


/**
 * Created by himphen on 21/5/16.
 */
class ToolSpeedTestFragment : BaseFragment(R.layout.fragment_tool_speed_test) {

    private var seriesDownload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var seriesUpload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))

    private var thread: Thread? = null
    private var loadingDialog: MaterialDialog? = null
    private var tempBlackList: HashSet<String> = HashSet()

    private var shouldStopNow = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            loadingDialog = MaterialDialog(it)
                .message(text = "Selecting best server based on ping...")
                .cancelable(false)

            // Init Download graphic
            graphViewDownload.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphViewDownload.gridLabelRenderer.isVerticalLabelsVisible = false
            graphViewDownload.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graphViewDownload.gridLabelRenderer.gridColor = Color.GRAY
            seriesDownload.thickness = 3
            seriesDownload.color = ContextCompat.getColor(it, R.color.green500)
            seriesDownload.isDrawBackground = true
            seriesDownload.backgroundColor = ContextCompat.getColor(it, R.color.green500a)
            graphViewDownload.addSeries(seriesDownload)
            graphViewDownload.viewport.isXAxisBoundsManual = true
            graphViewDownload.viewport.setMinX(0.0)
            graphViewDownload.viewport.setMaxX(10.0)

            // Init Upload graphic
            graphViewUpload.gridLabelRenderer.isHorizontalLabelsVisible = false
            graphViewUpload.gridLabelRenderer.isVerticalLabelsVisible = false
            graphViewUpload.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            graphViewUpload.gridLabelRenderer.gridColor = Color.GRAY
            seriesUpload.thickness = 3
            seriesUpload.color = ContextCompat.getColor(it, R.color.blue500)
            seriesUpload.isDrawBackground = true
            seriesUpload.backgroundColor = ContextCompat.getColor(it, R.color.blue500a)
            graphViewUpload.addSeries(seriesUpload)
            graphViewUpload.viewport.isXAxisBoundsManual = true
            graphViewUpload.viewport.setMinX(0.0)
            graphViewUpload.viewport.setMaxX(10.0)
        }

        startButton.setOnClickListener {
            startButton.isEnabled = false

            locationTv.text = "-"
            providerTv.text = "-"
            latencyTv.text = "-"
            initThread()
            thread?.start()
        }
    }

    private fun initThread() {
        thread = Thread(object : Runnable {
            val dec = DecimalFormat("#.##")

            @SuppressLint("SetTextI18n")
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
                var findServer: GetSpeedTestHostsHandler.Server =
                    getSpeedTestHostsHandler.serverList[0]

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
                    locationTv.text = findServer.name
                    providerTv.text = String.format(
                        "[%s km] %s",
                        DecimalFormat("#.##").format(distance / 1000),
                        findServer.sponsor
                    )
                }

                // Reset value, graphics
                activity?.runOnUiThread {
                    downloadTextView?.text = "0 Mbps"
                    uploadTextView?.text = "0 Mbps"
                }

                var pingTestStarted = false
                var pingTestFinished = false
                var downloadTestStarted = false
                var downloadTestFinished = false
                var uploadTestStarted = false
                var uploadTestFinished = false

                var lastXPositionDownload = 0.0
                var lastXPositionUpload = 0.0

                // Init Test
                val pingTest = PingTest(findServer.host.replace(":8080", ""), 1)
                val downloadTest = HttpDownloadTest(
                    uploadAddress.replace(
                        uploadAddress.split("/").toTypedArray()[uploadAddress.split("/")
                            .toTypedArray().size - 1], ""
                    )
                )
                val uploadTest = HttpUploadTest(uploadAddress)
                // Tests
                while (!shouldStopNow) {
                    if (!pingTestStarted) {
                        pingTest.start()
                        pingTestStarted = true
                    }
                    if (pingTestFinished && !downloadTestStarted) {
                        if (pingTest.avgRtt != 0.0) {
                            activity?.runOnUiThread {
                                latencyTv?.text = "${dec.format(pingTest.avgRtt)} ms"
                            }
                        }
                        downloadTest.start()
                        downloadTestStarted = true
                    }
                    if (downloadTestFinished && !uploadTestStarted) {
                        if (downloadTest.finalDownloadRate != 0.0) {
                            activity?.runOnUiThread {
                                downloadTextView?.text =
                                    "${dec.format(downloadTest.finalDownloadRate.roundTo(2))} Mbps"
                            }
                        }
                        uploadTest.start()
                        uploadTestStarted = true
                    }

                    // Ping Test
                    if (pingTestStarted && !pingTestFinished) {
                        activity?.runOnUiThread {
                            latencyTv?.text = "${dec.format(pingTest.instantRtt)} ms"
                        }
                    }

                    //Download Test
                    if (downloadTestStarted && !downloadTestFinished) {
                        val downloadRate: Double = downloadTest.instantDownloadRate
                        activity?.runOnUiThread {
                            downloadTextView.text = "${dec.format(downloadRate)} Mbps"
                        }
                        //Update chart
                        activity?.runOnUiThread {
                            seriesDownload.appendData(
                                DataPoint(
                                    lastXPositionDownload,
                                    downloadRate
                                ), false, 10000
                            )

                            if (graphViewDownload.viewport.getMaxX(true) > 10.0) {
                                graphViewDownload.viewport.setMaxX(
                                    graphViewDownload.viewport.getMaxX(
                                        true
                                    ) + 1
                                )
                            }
                            lastXPositionDownload++
                        }

                    }
                    // Upload Test
                    if (downloadTestFinished && !uploadTestFinished) {
                        val uploadRate: Double = uploadTest.instantUploadRate
                        activity?.runOnUiThread {
                            uploadTextView.text = "${dec.format(uploadRate)} Mbps"
                        }
                        // Update chart
                        activity?.runOnUiThread {
                            seriesUpload.appendData(
                                DataPoint(lastXPositionUpload, uploadRate),
                                false,
                                10000
                            )

                            if (graphViewUpload.viewport.getMaxX(true) > 10.0) {
                                graphViewUpload.viewport.setMaxX(
                                    graphViewUpload.viewport.getMaxX(
                                        true
                                    ) + 1
                                )
                            }

                            lastXPositionUpload++
                        }
                    }
                    //Test finished
                    if (pingTestFinished && downloadTestFinished && uploadTest.isFinished) {
                        if (uploadTest.getRoundedFinalUploadRate() != 0.0) { //Success
                            activity?.runOnUiThread {
                                uploadTextView?.text =
                                    "${dec.format(uploadTest.getRoundedFinalUploadRate())} Mbps"
                            }
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