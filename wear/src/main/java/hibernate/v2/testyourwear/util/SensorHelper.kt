package hibernate.v2.testyourwear.util

import android.hardware.Sensor

/**
 * Created by himphen on 21/5/16.
 */
object SensorHelper {
    fun getAccelerometerSensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        val arrayData = arrayOfNulls<String>(size)
        var i = 0
        arrayData[i] = reading
        i++
        arrayData[i] = sensor.name
        i++
        arrayData[i] = sensor.vendor
        i++
        arrayData[i] = sensor.version.toString()
        i++
        arrayData[i] = sensor.maximumRange.toString() + " m/s²"
        i++
        arrayData[i] = sensor.minDelay.toString() + " μs"
        i++
        arrayData[i] = "" + sensor.resolution + " m/s²"
        i++
        arrayData[i] = sensor.power.toString() + " mA"
        return arrayData[j]
    }

    fun getGravitySensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        return getAccelerometerSensorData(j, size, reading, sensor)
    }

    fun getPressureSensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        val arrayData = arrayOfNulls<String>(size)
        var i = 0
        arrayData[i] = reading
        i++
        arrayData[i] = sensor.name
        i++
        arrayData[i] = sensor.vendor
        i++
        arrayData[i] = sensor.version.toString()
        i++
        arrayData[i] = sensor.maximumRange.toString() + " hPa"
        i++
        arrayData[i] = sensor.minDelay.toString() + " μs"
        i++
        arrayData[i] = "" + sensor.resolution + " hPa"
        i++
        arrayData[i] = sensor.power.toString() + " mA"
        return arrayData[j]
    }

    fun getLightSensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        val arrayData = arrayOfNulls<String>(size)
        var i = 0
        arrayData[i] = reading
        i++
        arrayData[i] = sensor.name
        i++
        arrayData[i] = sensor.vendor
        i++
        arrayData[i] = sensor.version.toString()
        i++
        arrayData[i] = sensor.maximumRange.toString() + " lux"
        i++
        arrayData[i] = sensor.minDelay.toString() + " μs"
        i++
        arrayData[i] = "" + sensor.resolution + " lux"
        i++
        arrayData[i] = sensor.power.toString() + " mA"
        return arrayData[j]
    }

    fun getProximitySensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        val arrayData = arrayOfNulls<String>(size)
        var i = 0
        arrayData[i] = ""
        i++
        arrayData[i] = sensor.name
        i++
        arrayData[i] = sensor.vendor
        i++
        arrayData[i] = sensor.version.toString()
        i++
        arrayData[i] = sensor.maximumRange.toString() + " cm"
        i++
        arrayData[i] = sensor.minDelay.toString() + " cm"
        i++
        arrayData[i] = "" + sensor.resolution + " cm"
        i++
        arrayData[i] = sensor.power.toString() + " mA"
        return arrayData[j]
    }

    fun getMagneticSensorData(j: Int, size: Int, reading: String?, sensor: Sensor): String? {
        val arrayData = arrayOfNulls<String>(size)
        var i = 0
        arrayData[i] = reading
        i++
        arrayData[i] = sensor.name
        i++
        arrayData[i] = sensor.vendor
        i++
        arrayData[i] = sensor.version.toString()
        i++
        arrayData[i] = sensor.maximumRange.toString() + " μT"
        i++
        arrayData[i] = sensor.minDelay.toString() + " μT"
        i++
        arrayData[i] = "" + sensor.resolution + " μT"
        i++
        arrayData[i] = sensor.power.toString() + " mA"
        return arrayData[j]
    }
}