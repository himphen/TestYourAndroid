package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class InfoItem {
	private String titleText;
	private String contentText;

	public InfoItem() {
	}

	public InfoItem(String titleText, String contentText) {
		this.titleText = titleText;
		this.contentText = contentText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public String getTitleText() {
		return titleText;
	}

	public String getContentText() {
		return contentText;
	}
}