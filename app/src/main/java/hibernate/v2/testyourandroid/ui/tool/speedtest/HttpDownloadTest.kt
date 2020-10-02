package hibernate.v2.testyourandroid.ui.tool.speedtest

import hibernate.v2.testyourandroid.util.ext.roundTo
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class HttpDownloadTest(private val fileURL: String) : Thread() {
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var downloadElapsedTime = 0.0
    private var downloadedByte = 0
    var finalDownloadRate = 0.0
    var instantDownloadRate = 0.0
    private var timeout = 15

    var isFinished = false
    var shouldStopNow = false

    private fun setInstantDownloadRate(downloadedByte: Int, elapsedTime: Double) {
        instantDownloadRate = if (downloadedByte >= 0) {
            (downloadedByte * 8 / (1000 * 1000) / elapsedTime).roundTo(2)
        } else {
            0.00
        }
    }

    override fun run() {
        downloadedByte = 0
        val fileUrls: MutableList<String> = ArrayList()
        fileUrls.add((fileURL) + "random4000x4000.jpg")
        fileUrls.add((fileURL) + "random3000x3000.jpg")
        startTime = System.currentTimeMillis()
        outer@ for (link in fileUrls) {
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
                        if (downloadElapsedTime >= timeout && !shouldStopNow) {
                            break@outer
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
        finalDownloadRate = downloadedByte * 8 / (1000 * 1000.0) / downloadElapsedTime

        isFinished = true
    }
}