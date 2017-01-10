package hibernate.v2.testyourandroid.model;

/**
 * Created by himphen on 24/5/16.
 */

public class AppPermissionItem {

	private String permissionGroupLabel;
	private String permissionLabel;
	private String permissionDescription;

	public AppPermissionItem(String permissionGroupLabel, String permissionLabel, String permissionDescription) {
		this.permissionGroupLabel = permissionGroupLabel;
		this.permissionLabel = permissionLabel;
		this.permissionDescription = permissionDescription;
	}

	public String getPermissionGroupLabel() {
		return permissionGroupLabel;
	}

	public void setPermissionGroupLabel(String permissionGroupLabel) {
		this.permissionGroupLabel = permissionGroupLabel;
	}

	public String getPermissionLabel() {
		return permissionLabel;
	}

	public void setPermissionLabel(String permissionLabel) {
		this.permissionLabel = permissionLabel;
	}

	public String getPermissionDescription() {
		return permissionDescription;
	}

	public void setPermissionDescription(String permissionDescription) {
		this.permissionDescription = permissionDescription;
	}
}