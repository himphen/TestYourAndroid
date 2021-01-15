package hibernate.v2.testyourandroid.ui.tool.speedtest

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentToolSpeedTestBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.view.GetSpeedTestHostsHandler
import hibernate.v2.testyourandroid.ui.view.Server
import hibernate.v2.testyourandroid.util.ext.roundTo
import hibernate.v2.testyourandroid.util.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.text.DecimalFormat

/**
 * Created by himphen on 21/5/16.
 */
@SuppressLint("SetTextI18n")
class ToolSpeedTestFragment : BaseFragment(R.layout.fragment_tool_speed_test) {

    private val viewModel = ToolSpeedTestViewModel()

    private val binding by viewBinding(FragmentToolSpeedTestBinding::bind)

    private var seriesDownload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))
    private var seriesUpload = LineGraphSeries(arrayOf(DataPoint(0.0, 0.0)))

    private var loadingDialog: MaterialDialog? = null

    private var lastXPositionDownload = 0.0
    private var lastXPositionUpload = 0.0

    private var job: Job? = null
    val dec = DecimalFormat("#.##")

    init {
        lifecycleScope.launchWhenCreated {
            viewModel.pingInstantRtt.observe(this@ToolSpeedTestFragment, {
                binding.latencyTv.text = "${dec.format(it)} ms"
            })
            viewModel.pingAvgRtt.observe(this@ToolSpeedTestFragment, {
                binding.latencyTv.text = "${dec.format(it)} ms"
            })
            viewModel.instantDownloadRate.observe(this@ToolSpeedTestFragment, {
                binding.downloadTextView.text = "${dec.format(it.roundTo(2))} Mbps"

                seriesDownload.appendData(
                    DataPoint(lastXPositionDownload, it),
                    false, 10000
                )

                if (binding.graphViewDownload.viewport.getMaxX(true) > 10.0) {
                    binding.graphViewDownload.viewport.setMaxX(
                        binding.graphViewDownload.viewport.getMaxX(
                            true
                        ) + 1
                    )
                }
                lastXPositionDownload++
            })
            viewModel.instantUploadRate.observe(this@ToolSpeedTestFragment, {
                binding.uploadTextView.text = "${dec.format(it)} Mbps"

                seriesUpload.appendData(
                    DataPoint(lastXPositionUpload, it),
                    false, 10000
                )

                if (binding.graphViewUpload.viewport.getMaxX(true) > 10.0) {
                    binding.graphViewUpload.viewport.setMaxX(
                        binding.graphViewUpload.viewport.getMaxX(
                            true
                        ) + 1
                    )
                }
                lastXPositionUpload++
            })
            viewModel.finalDownloadRate.observe(this@ToolSpeedTestFragment, {
                binding.downloadTextView.text = "${dec.format(it.roundTo(2))} Mbps"
            })
            viewModel.finalUploadRate.observe(this@ToolSpeedTestFragment, {
                binding.uploadTextView.text = "${dec.format(it.roundTo(2))} Mbps"
            })

        }
    }

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
            initJob()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initJob() {
        job = GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                loadingDialog?.show()
            }

            var selfLat: Double? = null
            var selfLon: Double? = null
            val serverList = arrayListOf<Server>()

            retry(limitAttempts(10) + constantDelay(delayMillis = 500L)) {
                try {
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
                } catch (e: UnknownHostException) {
                }
            }

            if (selfLat == null || selfLon == null) {
                withContext(Dispatchers.Main) {
                    binding.startButton.isEnabled = true
                    loadingDialog?.dismiss()
                    showErrorDialog("No Connection...")
                }

                return@launch
            }

            retry(limitAttempts(10) + constantDelay(delayMillis = 500L)) {
                try {
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
                } catch (e: UnknownHostException) {
                }
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
                binding.locationTv.text = findServer.name
                binding.providerTv.text = String.format(
                    "[%s km] %s",
                    DecimalFormat("#.##").format(distance / 1000),
                    findServer.sponsor
                )
            }

            // Reset value, graphics
            withContext(Dispatchers.Main) {
                binding.downloadTextView.text = "0 Mbps"
                binding.uploadTextView.text = "0 Mbps"
            }

            // Init Test
            viewModel.testPing(pingAddress, 1)
            viewModel.testDownload(downloadAddress)
            viewModel.testUpload(uploadAddress)

            // Button re-activated at the end of thread
            withContext(Dispatchers.Main) {
                binding.startButton.isEnabled = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
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
        fun newInstance(): ToolSpeedTestFragment = ToolSpeedTestFragment()
    }
}