package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class GridItem {
	private String mainText;
	private int mainImageId;
	private Class intentClass;
	private String actionType;

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getMainText() {
		return mainText;
	}

	public int getMainImageId() {
		return mainImageId;
	}

	public Class getIntentClass() {
		return intentClass;
	}

	public GridItem(String mainText, int mainImageId, Class intentClass) {
		this.mainText = mainText;
		this.mainImageId = mainImageId;
		this.intentClass = intentClass;
	}

	public GridItem(String mainText, int mainImageId, String actionType) {
		this.mainText = mainText;
		this.mainImageId = mainImageId;
		this.actionType = actionType;
	}
}