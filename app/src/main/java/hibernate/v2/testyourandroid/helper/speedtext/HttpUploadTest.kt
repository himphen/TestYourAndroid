package hibernate.v2.testyourandroid.helper.speedtext

import com.orhanobut.logger.Logger
import hibernate.v2.testyourandroid.helper.roundTo
import java.io.DataOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class HttpUploadTest(private val fileURL: String) : Thread() {
    private var startTime: Long = 0
    private var finalUploadRate = 0.0

    var isFinished = false

    val instantUploadRate: Double
        get() {
            try {
                BigDecimal(uploadedKByte)
            } catch (ex: Exception) {
                return 0.0
            }
            return if (uploadedKByte >= 0) {
                val now = System.currentTimeMillis()
                val elapsedTime = (now - startTime) / 1000.0
                (uploadedKByte * 8 / 1000 / elapsedTime).roundTo(2)
            } else {
                0.0
            }
        }

    fun getRoundedFinalUploadRate(): Double {
        return finalUploadRate.roundTo(2)
    }

    override fun run() {
        try {
            val url = URL(fileURL)
            uploadedKByte = 0
            startTime = System.currentTimeMillis()
            val executor = Executors.newFixedThreadPool(2)
            for (i in 0..1) {
                if (shouldStopNow) {
                    break
                }
                executor.execute(HandlerUpload(url))
            }
            executor.shutdown()
            while (!executor.isTerminated) {
                try {
                    sleep(100)
                } catch (ex: InterruptedException) {
                }
            }
            val now = System.currentTimeMillis()
            val uploadElapsedTime = (now - startTime) / 1000.0
            finalUploadRate = (uploadedKByte / 1000.0 * 8) / uploadElapsedTime
        } catch (ex: InterruptedException) {
            Logger.d("upload thread InterruptedException")
        }
        isFinished = true

    }

    companion object {
        var uploadedKByte = 0
        var shouldStopNow = false
    }

    internal class HandlerUpload(private var url: URL) : Thread() {
        override fun run() {
            val buffer = ByteArray(150 * 1024)
            val startTime = System.currentTimeMillis()
            val timeout = 10
            while (true) {
                try {
                    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                    conn.doOutput = true
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Connection", "Keep-Alive")
                    val dos = DataOutputStream(conn.outputStream)
                    dos.write(buffer, 0, buffer.size)
                    dos.flush()

                    uploadedKByte += buffer.size / 1024.0.toInt()
                    val endTime = System.currentTimeMillis()
                    val uploadElapsedTime = (endTime - startTime) / 1000.0
                    if (uploadElapsedTime >= timeout && !shouldStopNow) {
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
}