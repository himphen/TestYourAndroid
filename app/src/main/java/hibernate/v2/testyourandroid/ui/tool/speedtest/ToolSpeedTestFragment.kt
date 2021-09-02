package hibernate.v2.testyourandroid.ui.tool.speedtest

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolSpeedTestBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.view.GetSpeedTestHostsHandler
import hibernate.v2.testyourandroid.ui.view.Server
import hibernate.v2.testyourandroid.util.ext.roundTo
import hibernate.v2.testyourandroid.util.retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat

/**
 * Created by himphen on 21/5/16.
 */
@SuppressLint("SetTextI18n")
class ToolSpeedTestFragment : BaseFragment<FragmentToolSpeedTestBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentToolSpeedTestBinding =
        FragmentToolSpeedTestBinding.inflate(inflater, container, false)

    private val viewModel = ToolSpeedTestViewModel()

    private var seriesDownload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var seriesUpload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))

    private var loadingDialog: AlertDialog? = null

    private var lastXPositionDownload = 0.0
    private var lastXPositionUpload = 0.0

    private var job: Job? = null
    val dec = DecimalFormat("#.##")

    init {
        lifecycleScope.launchWhenCreated {
            viewModel.pingInstantRtt.observe(this@ToolSpeedTestFragment, {
                viewBinding?.latencyTv?.text = "${dec.format(it)} ms"
            })
            viewModel.pingAvgRtt.observe(this@ToolSpeedTestFragment, {
                viewBinding?.latencyTv?.text = "${dec.format(it)} ms"
            })
            viewModel.instantDownloadRate.observe(this@ToolSpeedTestFragment, {
                viewBinding?.let { viewBinding ->
                    viewBinding.downloadTextView.text = "${dec.format(it.roundTo(2))} Mbps"

                    seriesDownload.appendData(
                        DataPoint(lastXPositionDownload, it),
                        false, 10000
                    )

                    if (viewBinding.graphViewDownload.viewport.getMaxX(true) > 10.0) {
                        viewBinding.graphViewDownload.viewport.setMaxX(
                            viewBinding.graphViewDownload.viewport.getMaxX(
                                true
                            ) + 1
                        )
                    }
                    lastXPositionDownload++
                }
            })
            viewModel.instantUploadRate.observe(this@ToolSpeedTestFragment, {
                viewBinding?.let { viewBinding ->
                    viewBinding.uploadTextView.text = "${dec.format(it)} Mbps"

                    seriesUpload.appendData(
                        DataPoint(lastXPositionUpload, it),
                        false, 10000
                    )

                    if (viewBinding.graphViewUpload.viewport.getMaxX(true) > 10.0) {
                        viewBinding.graphViewUpload.viewport.setMaxX(
                            viewBinding.graphViewUpload.viewport.getMaxX(
                                true
                            ) + 1
                        )
                    }
                    lastXPositionUpload++
                }
            })
            viewModel.finalDownloadRate.observe(this@ToolSpeedTestFragment, {
                viewBinding?.downloadTextView?.text = "${dec.format(it.roundTo(2))} Mbps"
            })
            viewModel.finalUploadRate.observe(this@ToolSpeedTestFragment, {
                viewBinding?.uploadTextView?.text = "${dec.format(it.roundTo(2))} Mbps"
            })

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.let { viewBinding ->

            context?.let {
                loadingDialog = MaterialAlertDialogBuilder(it)
                    .setMessage("Selecting best server based on ping...")
                    .setCancelable(false)
                    .create()

                // Init Download graphic
                viewBinding.graphViewDownload.gridLabelRenderer.isHorizontalLabelsVisible = false
                viewBinding.graphViewDownload.gridLabelRenderer.isVerticalLabelsVisible = false
                viewBinding.graphViewDownload.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                viewBinding.graphViewDownload.gridLabelRenderer.gridColor = Color.GRAY
                seriesDownload.thickness = 3
                seriesDownload.color = ContextCompat.getColor(it, R.color.lineColor4)
                seriesDownload.isDrawBackground = true
                seriesDownload.backgroundColor = ContextCompat.getColor(it, R.color.lineColor4A)
                viewBinding.graphViewDownload.addSeries(seriesDownload)
                viewBinding.graphViewDownload.viewport.isXAxisBoundsManual = true
                viewBinding.graphViewDownload.viewport.setMinX(0.0)
                viewBinding.graphViewDownload.viewport.setMaxX(10.0)

                // Init Upload graphic
                viewBinding.graphViewUpload.gridLabelRenderer.isHorizontalLabelsVisible = false
                viewBinding.graphViewUpload.gridLabelRenderer.isVerticalLabelsVisible = false
                viewBinding.graphViewUpload.gridLabelRenderer.gridStyle =
                    GridLabelRenderer.GridStyle.HORIZONTAL
                viewBinding.graphViewUpload.gridLabelRenderer.gridColor = Color.GRAY
                seriesUpload.thickness = 3
                seriesUpload.color = ContextCompat.getColor(it, R.color.lineColor3)
                seriesUpload.isDrawBackground = true
                seriesUpload.backgroundColor = ContextCompat.getColor(it, R.color.lineColor3A)
                viewBinding.graphViewUpload.addSeries(seriesUpload)
                viewBinding.graphViewUpload.viewport.isXAxisBoundsManual = true
                viewBinding.graphViewUpload.viewport.setMinX(0.0)
                viewBinding.graphViewUpload.viewport.setMaxX(10.0)
            }

            viewBinding.startButton.setOnClickListener {
                viewBinding.startButton.isEnabled = false

                viewBinding.locationTv.text = "-"
                viewBinding.providerTv.text = "-"
                viewBinding.latencyTv.text = "-"
                initJob()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initJob() {
        job = lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                loadingDialog?.show()
            }

            var selfLat: Double? = null
            var selfLon: Double? = null
            val serverList = arrayListOf<Server>()

            try {
                retry(10) {
                    // Get latitude, longitude
                    val url = URL("https://www.speedtest.net/speedtest-config.php")
                    val urlConnection = url.openConnection() as HttpURLConnection
                    val code = urlConnection.responseCode
                    if (code == 200) {
                        GetSpeedTestHostsHandler.parseClient(urlConnection.inputStream)?.let {
                            selfLat = it.lat.toDouble()
                            selfLon = it.lon.toDouble()
                        }
                    }
                }
            } catch (e: Exception) {
            }

            if (selfLat == null || selfLon == null) {
                withContext(Dispatchers.Main) {
                    viewBinding?.startButton?.isEnabled = true
                    loadingDialog?.dismiss()
                    showErrorDialog("No Connection...")
                }

                return@launch
            }

            try {
                retry(10) {
                    // Best server
                    val url = URL("https://www.speedtest.net/speedtest-servers-static.php")
                    val urlConnection = url.openConnection() as HttpURLConnection
                    val code = urlConnection.responseCode
                    if (code == 200) {
                        val list: List<Server> =
                            GetSpeedTestHostsHandler.parseServer(urlConnection.inputStream)
                        for (value: Server in list) {
                            serverList.add(value)
                        }
                    }
                }
            } catch (e: Exception) {
            }

            if (serverList.isEmpty()) {
                withContext(Dispatchers.Main) {
                    viewBinding?.startButton?.isEnabled = true
                    loadingDialog?.dismiss()
                    showErrorDialog("No Connection...")
                }

                return@launch
            }

            withContext(Dispatchers.Main) {
                loadingDialog?.dismiss()
                seriesDownload.resetData(arrayOf(DataPoint(0.0, 0.0)))
                seriesUpload.resetData(arrayOf(DataPoint(0.0, 0.0)))
            }

            // Find closest server
            var tmp = 19349458.0
            var dist = 0.0
            var findServer = serverList[0]

            val source = Location("Source")
            source.latitude = selfLat!!
            source.longitude = selfLon!!

            for (server in serverList) {
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

            val pingAddress = findServer.host.replace(":8080", "")
            val uploadAddress = findServer.uploadAddress
            val downloadAddress = uploadAddress.replace(
                uploadAddress.split("/").toTypedArray()[uploadAddress.split("/")
                    .toTypedArray().size - 1], ""
            )
            val distance = dist
            withContext(Dispatchers.Main) {
                viewBinding?.locationTv?.text = findServer.name
                viewBinding?.providerTv?.text = String.format(
                    "[%s km] %s",
                    DecimalFormat("#.##").format(distance / 1000),
                    findServer.sponsor
                )
            }

            // Reset value, graphics
            withContext(Dispatchers.Main) {
                viewBinding?.downloadTextView?.text = "0 Mbps"
                viewBinding?.uploadTextView?.text = "0 Mbps"
            }

            // Init Test
            viewModel.testPing(pingAddress, 1)
            viewModel.testDownload(downloadAddress)
            viewModel.testUpload(uploadAddress)

            // Button re-activated at the end of thread
            withContext(Dispatchers.Main) {
                viewBinding?.startButton?.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }

    private fun showErrorDialog(content: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.ui_error)
                .setMessage(content)
                .setPositiveButton(R.string.ui_okay, null)
                .setCancelable(false)
                .show()
        }
    }

    companion object {
        fun newInstance(): ToolSpeedTestFragment = ToolSpeedTestFragment()
    }
}