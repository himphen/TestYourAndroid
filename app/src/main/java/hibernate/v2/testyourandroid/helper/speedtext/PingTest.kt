package hibernate.v2.testyourandroid.helper.speedtext

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author erdigurbuz
 */
class PingTest(
        private val serverIpAddress: String,
        private val pingTryCount: Int
) : Thread() {
    var instantRtt = 0.0
    var avgRtt = 0.0

    var isFinished = false
    var shouldStopNow = false

    override fun run() {
        try {
            val ps = ProcessBuilder("ping", "-c $pingTryCount", serverIpAddress)
            ps.redirectErrorStream(true)
            val pr = ps.start()
            val bufferedReader = BufferedReader(InputStreamReader(pr.inputStream))
            var line: String
            while (bufferedReader.readLine().also { line = it } != null) {
                if (shouldStopNow) {
                    break
                }
                if (line.contains("icmp_seq")) {
                    instantRtt = line.split(" ").toTypedArray()[line.split(" ").toTypedArray().size - 2].replace("time=", "").toDouble()
                }
                if (line.startsWith("rtt ")) {
                    avgRtt = line.split("/").toTypedArray()[4].toDouble()
                    break
                }
            }
            pr.waitFor()
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isFinished = true
    }
}