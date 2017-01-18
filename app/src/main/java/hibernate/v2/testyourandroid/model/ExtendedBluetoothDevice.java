package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class ExtendedBluetoothDevice {

	private String name;
	private String riss;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRiss() {
		return riss;
	}

	public void setRiss(int riss) {
		this.riss = "-" + riss + " dBm";
	}
}