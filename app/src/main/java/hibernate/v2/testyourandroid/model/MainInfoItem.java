package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class MainInfoItem {
	private String mainText;
	private Class intentClass;

	public String getMainText() {
		return mainText;
	}

	public Class getIntentClass() {
		return intentClass;
	}

	public MainInfoItem(String mainText, Class intentClass) {
		this.mainText = mainText;
		this.intentClass = intentClass;
	}
}