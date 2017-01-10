package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class MainItem {
	private String mainText;
	private int mainImageId;
	private Class intentClass;

	public String getMainText() {
		return mainText;
	}

	public int getMainImageId() {
		return mainImageId;
	}

	public Class getIntentClass() {
		return intentClass;
	}

	public MainItem(String mainText, int mainImageId, Class intentClass) {
		this.mainText = mainText;
		this.mainImageId = mainImageId;
		this.intentClass = intentClass;
	}
}