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
import hibernate.v2.testyourandroid.databinding.FragmentToolSpeedTestBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.view.GetSpeedTestHostsHandler
import hibernate.v2.testyourandroid.util.ext.roundTo
import hibernate.v2.testyourandroid.util.viewBinding
import java.text.DecimalFormat

/**
 * Created by himphen on 21/5/16.
 */
class ToolSpeedTestFragment : BaseFragment(R.layout.fragment_tool_speed_test) {

    private val binding by viewBinding(FragmentToolSpeedTestBinding::bind)

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
            binding.graphViewDownload.gridLabelRenderer.isHorizontalLabelsVisible = false
            binding.graphViewDownload.gridLabelRenderer.isVerticalLabelsVisible = false
            binding.graphViewDownload.gridLabelRenderer.gridStyle =
                GridLabelRenderer.GridStyle.HORIZONTAL
            binding.graphViewDownload.gridLabelRenderer.gridColor = Color.GRAY
            seriesDownload.thickness = 3
            seriesDownload.color = ContextCompat.getColor(it, R.color.lineColor4)
            seriesDownload.isDrawBackground = true
            seriesDownload.backgroundColor = ContextCompat.getColor(it, R.color.lineColor4A)
            binding.graphViewDownload.addSeries(seriesDownload)
            binding.graphViewDownload.viewport.isXAxisBoundsManual = true
            binding.graphViewDownload.viewport.setMinX(0.0)
            binding.graphViewDownload.viewport.setMaxX(10.0)

            // Init Upload graphic
            binding.graphViewUpload.gridLabelRenderer.isHorizontalLabelsVisible = false
            binding.graphViewUpload.gridLabelRenderer.isVerticalLabelsVisible = false
            binding.graphViewUpload.gridLabelRenderer.gridStyle =
                GridLabelRenderer.GridStyle.HORIZONTAL
            binding.graphViewUpload.gridLabelRenderer.gridColor = Color.GRAY
            seriesUpload.thickness = 3
            seriesUpload.color = ContextCompat.getColor(it, R.color.lineColor3)
            seriesUpload.isDrawBackground = true
            seriesUpload.backgroundColor = ContextCompat.getColor(it, R.color.lineColor3A)
            binding.graphViewUpload.addSeries(seriesUpload)
            binding.graphViewUpload.viewport.isXAxisBoundsManual = true
            binding.graphViewUpload.viewport.setMinX(0.0)
            binding.graphViewUpload.viewport.setMaxX(10.0)
        }

        binding.startButton.setOnClickListener {
            binding.startButton.isEnabled = false

            binding.locationTv.text = "-"
            binding.providerTv.text = "-"
            binding.latencyTv.text = "-"
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
                            binding.startButton.isEnabled = true
                            loadingDialog?.dismiss()
                        }
                        getSpeedTestHostsHandler.interrupt()
                        return
                    }
                    if (timeCount <= 0) {
                        activity?.runOnUiThread {
                            showErrorDialog("No Connection...")
                            binding.startButton.isEnabled = true
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
                    binding.startButton.isEnabled = true
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
                    binding.locationTv.text = findServer.name
                    binding.providerTv.text = String.format(
                        "[%s km] %s",
                        DecimalFormat("#.##").format(distance / 1000),
                        findServer.sponsor
                    )
                }

                // Reset value, graphics
                activity?.runOnUiThread {
                    binding.downloadTextView.text = "0 Mbps"
                    binding.uploadTextView.text = "0 Mbps"
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
                                binding.latencyTv.text = "${dec.format(pingTest.avgRtt)} ms"
                            }
                        }
                        downloadTest.start()
                        downloadTestStarted = true
                    }
                    if (downloadTestFinished && !uploadTestStarted) {
                        if (downloadTest.finalDownloadRate != 0.0) {
                            activity?.runOnUiThread {
                                binding.downloadTextView.text =
                                    "${dec.format(downloadTest.finalDownloadRate.roundTo(2))} Mbps"
                            }
                        }
                        uploadTest.start()
                        uploadTestStarted = true
                    }

                    // Ping Test
                    if (pingTestStarted && !pingTestFinished) {
                        activity?.runOnUiThread {
                            binding.latencyTv.text = "${dec.format(pingTest.instantRtt)} ms"
                        }
                    }

                    //Download Test
                    if (downloadTestStarted && !downloadTestFinished) {
                        val downloadRate: Double = downloadTest.instantDownloadRate
                        activity?.runOnUiThread {
                            binding.downloadTextView.text = "${dec.format(downloadRate)} Mbps"
                        }
                        //Update chart
                        activity?.runOnUiThread {
                            seriesDownload.appendData(
                                DataPoint(
                                    lastXPositionDownload,
                                    downloadRate
                                ), false, 10000
                            )

                            if (binding.graphViewDownload.viewport.getMaxX(true) > 10.0) {
                                binding.graphViewDownload.viewport.setMaxX(
                                    binding.graphViewDownload.viewport.getMaxX(
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
                            binding.uploadTextView.text = "${dec.format(uploadRate)} Mbps"
                        }
                        // Update chart
                        activity?.runOnUiThread {
                            seriesUpload.appendData(
                                DataPoint(lastXPositionUpload, uploadRate),
                                false,
                                10000
                            )

                            if (binding.graphViewUpload.viewport.getMaxX(true) > 10.0) {
                                binding.graphViewUpload.viewport.setMaxX(
                                    binding.graphViewUpload.viewport.getMaxX(
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
                                binding.uploadTextView.text =
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
                    binding.startButton.isEnabled = true
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