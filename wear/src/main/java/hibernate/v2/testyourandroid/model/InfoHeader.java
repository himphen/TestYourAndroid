package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class InfoHeader {
	private String titleText;

	public InfoHeader(String titleText) {
		this.titleText = titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getTitleText() {
		return titleText;
	}
}