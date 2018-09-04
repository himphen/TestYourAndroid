package hibernate.v2.testyourandroid.helper;

import android.hardware.Sensor;

/**
 * Created by himphen on 21/5/16.
 */
public class SensorHelper {

	public static String getAccelerometerSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange()) + " m/s²";
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " μs";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution()) + " m/s²";
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getGravitySensorData(int j, int size, String reading, Sensor sensor) {
		return getAccelerometerSensorData(j, size, reading, sensor);
	}

	public static String getPressureSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange()) + " hPa";
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " μs";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution()) + " hPa";
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getLightSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange()) + " lux";
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " μs";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution()) + " lux";
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getProximitySensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = "";
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange()) + " cm";
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " μs";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution()) + " cm";
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getMagneticSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange()) + " μT";
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " μT";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution()) + " μT";
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getStepCounterSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange());
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " s";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution());
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getTemperatureCounterSensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange());
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " s";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution());
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}

	public static String getHumiditySensorData(int j, int size, String reading, Sensor sensor) {
		String[] arrayData = new String[size];
		int i = 0;
		arrayData[i] = reading;
		i++;
		arrayData[i] = sensor.getName();
		i++;
		arrayData[i] = sensor.getVendor();
		i++;
		arrayData[i] = String.valueOf(sensor.getVersion());
		i++;
		arrayData[i] = String.valueOf(sensor.getMaximumRange());
		i++;
		arrayData[i] = String.valueOf(sensor.getMinDelay()) + " s";
		i++;
		arrayData[i] = "" + String.valueOf(sensor.getResolution());
		i++;
		arrayData[i] = String.valueOf(sensor.getPower()) + " mA";

		return arrayData[j];
	}
}
