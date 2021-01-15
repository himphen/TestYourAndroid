package hibernate.v2.testyourandroid.ui.tool.speedtest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hibernate.v2.testyourandroid.util.ext.roundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class ToolSpeedTestViewModel : ViewModel() {

    var pingInstantRtt = MutableLiveData<Double>()
    var pingAvgRtt = MutableLiveData<Double>()

    fun testPing(
        serverIpAddress: String,
        pingTryCount: Int
    ) {
        val ps = ProcessBuilder("ping", "-c $pingTryCount", serverIpAddress)
        ps.redirectErrorStream(true)
        val pr = ps.start()
        val bufferedReader = BufferedReader(InputStreamReader(pr.inputStream))
        bufferedReader.forEachLine { line ->
            try {
                if (line.contains("icmp_seq")) {
                    pingInstantRtt.postValue(
                        line.split(" ").toTypedArray()[line.split(" ")
                            .toTypedArray().size - 2].replace("time=", "").toDouble()
                    )
                }
                if (line.startsWith("rtt ")) {
                    pingAvgRtt.postValue(line.split("/").toTypedArray()[4].toDouble())
                    return@forEachLine
                }
            } catch (e: NumberFormatException) {
            }
        }
        pr.waitFor()
        bufferedReader.close()
    }

    var finalDownloadRate = MutableLiveData<Double>()
    var instantDownloadRate = MutableLiveData<Double>()
    private var timeout = 15

    private fun setInstantDownloadRate(downloadedByte: Int, elapsedTime: Double) {
        instantDownloadRate.postValue(
            if (downloadedByte >= 0) {
                (downloadedByte * 8 / (1000 * 1000) / elapsedTime).roundTo(2)
            } else {
                0.00
            }
        )
    }

    fun testDownload(fileURL: String) {
        val fileUrls: MutableList<String> = ArrayList()
        fileUrls.add((fileURL) + "random4000x4000.jpg")
        fileUrls.add((fileURL) + "random3000x3000.jpg")
        val startTime = System.currentTimeMillis()
        var endTime: Long

        var downloadElapsedTime: Double
        var downloadedByte = 0
        for (link in fileUrls) {
            try {
                val url = URL(link)
                val httpConn = url.openConnection() as HttpURLConnection

                if (httpConn.responseCode == HttpURLConnection.HTTP_OK) {
                    val buffer = ByteArray(10240)
                    val inputStream = httpConn.inputStream
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        downloadedByte += len
                        endTime = System.currentTimeMillis()
                        downloadElapsedTime = (endTime - startTime) / 1000.0
                        setInstantDownloadRate(downloadedByte, downloadElapsedTime)
                        if (downloadElapsedTime >= timeout) {
                            break
                        }
                    }
                    inputStream.close()
                    httpConn.disconnect()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        endTime = System.currentTimeMillis()
        downloadElapsedTime = (endTime - startTime) / 1000.0
        finalDownloadRate.postValue(downloadedByte * 8 / (1000 * 1000.0) / downloadElapsedTime)
    }

    var finalUploadRate = MutableLiveData<Double>()
    var instantUploadRate = MutableLiveData<Double>()

    suspend fun testUpload(fileURL: String) {
        val startTime = System.currentTimeMillis()
        var uploadedKByte = 0

        coroutineScope {
            repeat(2) {
                launch(Dispatchers.IO) { // 1
                    val buffer = ByteArray(150 * 1024)
                    val timeout = 10
                    while (true) {
                        try {
                            val url = URL(fileURL)
                            val conn: HttpURLConnection =
                                url.openConnection() as HttpURLConnection
                            conn.doOutput = true
                            conn.requestMethod = "POST"
                            conn.setRequestProperty("Connection", "Keep-Alive")
                            val dos = DataOutputStream(conn.outputStream)
                            dos.write(buffer, 0, buffer.size)
                            dos.flush()

                            uploadedKByte += buffer.size / 1024.0.toInt()

                            instantUploadRate.postValue(
                                try {
                                    BigDecimal(uploadedKByte)

                                    if (uploadedKByte >= 0) {
                                        val now = System.currentTimeMillis()
                                        val elapsedTime = (now - startTime) / 1000.0
                                        (uploadedKByte * 8 / 1000 / elapsedTime).roundTo(2)
                                    } else {
                                        0.0
                                    }
                                } catch (ex: Exception) {
                                    0.0
                                }
                            )

                            val endTime = System.currentTimeMillis()
                            val uploadElapsedTime = (endTime - startTime) / 1000.0
                            if (uploadElapsedTime >= timeout) {
                                break
                            }
                            dos.close()
                            conn.disconnect()
                        } catch (ex: IOException) {
                            break
                        }
                    }
                }
            }
        } // 2

        val now = System.currentTimeMillis()
        val uploadElapsedTime = (now - startTime) / 1000.0
        finalUploadRate.postValue((uploadedKByte / 1000.0 * 8) / uploadElapsedTime)
    }
}