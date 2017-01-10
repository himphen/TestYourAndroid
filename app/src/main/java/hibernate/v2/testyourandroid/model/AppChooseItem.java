package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class AppChooseItem extends InfoItem {
	private int appType;

	public int getAppType() {
		return appType;
	}

	public AppChooseItem(String titleText, String contentText, int appType) {
		super(titleText, contentText);
		this.appType = appType;
	}
}