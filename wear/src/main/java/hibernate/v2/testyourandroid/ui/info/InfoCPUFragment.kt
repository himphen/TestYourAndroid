package hibernate.v2.testyourandroid.ui.info

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.formatBitSize
import hibernate.v2.testyourandroid.model.InfoHeader
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.util.UUID
import java.util.regex.Pattern

/**
 * Created by himphen on 21/5/16.
 */
class InfoCPUFragment : BaseFragment() {

    private lateinit var memoryArray: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        init()
    }

    private fun init() {
        val list: MutableList<InfoItem> = ArrayList()
        val stringArray = resources.getStringArray(R.array.info_cpu_string_array)
        memoryArray = resources.getStringArray(R.array.memory_string_array)
        for (i in stringArray.indices) {
            list.add(InfoItem(stringArray[i], getData(i)))
        }
        val adapter = InfoItemAdapter(list)
        adapter.header = InfoHeader(activity?.title.toString())
        rvlist.adapter = adapter
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> (getCPUInfo(CPU_MIN).toInt() / 1000).toString() + "MHz"
                1 -> (getCPUInfo(CPU_MAX).toInt() / 1000).toString() + "MHz"
                2 -> numCores.toString()
                3 -> {
                    val romMemory = romMemory
                    (memoryArray[0] + formatBitSize(romMemory[0]) + "\n"
                            + memoryArray[1] + formatBitSize(romMemory[1]) + "\n"
                            + memoryArray[2] + formatBitSize(romMemory[0] - romMemory[1]))
                }
                4 -> {
                    val sDCardMemory = sDCardMemory
                    memoryArray[0] + formatBitSize(sDCardMemory[0]) + "\n" +
                            memoryArray[1] + formatBitSize(sDCardMemory[1]) + "\n" +
                            memoryArray[2] + formatBitSize(sDCardMemory[0] - sDCardMemory[1])
                }
                5 -> ramMemory
                6 -> allMemory
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }

    private val allMemory: String
        get() {
            val str1 = "/proc/meminfo"
            var str2: String?
            var str3 = StringBuilder()
            try {
                val fr = FileReader(str1)
                val localBufferedReader = BufferedReader(fr, 8192)
                while (localBufferedReader.readLine().also { str2 = it } != null) {
                    str3.append(str2).append("\n")
                }
                str3 = StringBuilder(str3.substring(0, str3.length - 1))
                localBufferedReader.close()
            } catch (ignored: IOException) {
            }
            str3 = StringBuilder(str3.toString().replace(" ".toRegex(), ""))
            str3 = StringBuilder(str3.toString().replace(":".toRegex(), ": "))
            return str3.toString()
        }

    private fun getCPUInfo(type: String): String {
        var filename = ""
        var single = 0
        when (type) {
            CPU_MIN -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
            CPU_MAX -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            CPU_CUR -> filename = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
            else -> single = 1
        }
        val list = ArrayList<String>()
        val file = File(filename)
        if (file.exists()) {
            try {
                val bufferedReader = BufferedReader(FileReader(file))
                for (line in bufferedReader.readLine()) {
                    list.add(line.toString())
                }
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val sd = StringBuilder()
        for (e in list) {
            sd.append(e)
            if (single == 1) {
                sd.append("\n")
            }
        }
        return sd.toString()
    }// Default to return 1 core// Get directory containing CPU info
    // Filter to only list the devices we care about
    // Return the number of cores (virtual CPU devices)
// Check if filename is "cpu", followed by a single digit number

    // Private Class to display only CPU devices in the directory listing
    private val numCores: Int
        get() { // Private Class to display only CPU devices in the directory listing
            class CpuFilter : FileFilter {
                override fun accept(pathname: File): Boolean { // Check if filename is "cpu", followed by a single digit number
                    return Pattern.matches("cpu[0-9]", pathname.name)
                }
            }
            return try { // Get directory containing CPU info
                val dir = File("/sys/devices/system/cpu/")
                // Filter to only list the devices we care about
                val files = dir.listFiles(CpuFilter())
                // Return the number of cores (virtual CPU devices)
                files?.size ?: 1
            } catch (e: Exception) { // Default to return 1 core
                1
            }
        }

    private val ramMemory: String
        get() {
            context?.let { context ->
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)
                val totalMem = memoryInfo.totalMem
                var text = ""
                text += memoryArray[0] + formatBitSize(totalMem) + "\n"
                text += memoryArray[1] + formatBitSize(memoryInfo.availMem) + "\n"
                text += memoryArray[2] + formatBitSize(totalMem - memoryInfo.availMem) + "\n"
                text += memoryArray[3] + formatBitSize(memoryInfo.threshold)
                return text
            }
            return ""
        }

    private val romMemory: LongArray
        get() {
            val romInfo = LongArray(2)
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            romInfo[0] = stat.blockSizeLong * stat.blockCountLong
            romInfo[1] = stat.blockSizeLong * stat.availableBlocksLong
            return romInfo
        }

    @Suppress("DEPRECATION")
    private val sDCardMemory: LongArray
        get() {
            val sdCardInfo = LongArray(2)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageManager = context?.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                val storageStatsManager = context?.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                val storageVolumes = storageManager.storageVolumes

                for (storageVolume in storageVolumes) {
                    val uuid = storageVolume.uuid?.let { UUID.fromString(it) }
                            ?: StorageManager.UUID_DEFAULT
                    storageStatsManager.getFreeBytes(uuid)
                    storageStatsManager.getTotalBytes(uuid)

                    sdCardInfo[0] = storageStatsManager.getTotalBytes(uuid)
                    sdCardInfo[1] = storageStatsManager.getFreeBytes(uuid)
                }
            } else {
                val state = Environment.getExternalStorageState()
                if (Environment.MEDIA_MOUNTED == state) {
                    val path = Environment.getExternalStorageDirectory()
                    val stat = StatFs(path.path)
                    sdCardInfo[0] = stat.blockSizeLong * stat.blockCountLong
                    sdCardInfo[1] = stat.blockSizeLong * stat.availableBlocksLong
                }
            }
            return sdCardInfo
        }

    companion object {
        private const val CPU_MIN = "CPUMIN"
        private const val CPU_MAX = "CPUMAX"
        private const val CPU_CUR = "CPUCUR"
    }
}