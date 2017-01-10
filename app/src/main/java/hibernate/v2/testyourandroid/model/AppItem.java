package hibernate.v2.testyourandroid.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by himphen on 24/5/16.
 */

public class AppItem implements Parcelable {
	private String appName;
	private String packageName;
	private String sourceDir;
	private String publicSourceDir;
	private String launchActivity;
	private String versionName;
	private String versionCode;
	private long firstInstallTime;
	private Drawable icon;
	private boolean isSystemApp;

	public AppItem() {
	}

	protected AppItem(Parcel in) {
		appName = in.readString();
		packageName = in.readString();
		sourceDir = in.readString();
		publicSourceDir = in.readString();
		launchActivity = in.readString();
		versionName = in.readString();
		versionCode = in.readString();
		firstInstallTime = in.readLong();
		isSystemApp = in.readByte() != 0;
	}

	public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
		@Override
		public AppItem createFromParcel(Parcel in) {
			return new AppItem(in);
		}

		@Override
		public AppItem[] newArray(int size) {
			return new AppItem[size];
		}
	};

	public String getPublicSourceDir() {
		return publicSourceDir;
	}

	public void setPublicSourceDir(String publicSourceDir) {
		this.publicSourceDir = publicSourceDir;
	}

	public boolean isSystemApp() {
		return isSystemApp;
	}

	public void setSystemApp(boolean systemApp) {
		isSystemApp = systemApp;
	}

	public long getFirstInstallTime() {
		return firstInstallTime;
	}

	public void setFirstInstallTime(long firstInstallTime) {
		this.firstInstallTime = firstInstallTime;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getLaunchActivity() {
		return launchActivity;
	}

	public void setLaunchActivity(String launchActivity) {
		this.launchActivity = launchActivity;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(appName);
		parcel.writeString(packageName);
		parcel.writeString(sourceDir);
		parcel.writeString(publicSourceDir);
		parcel.writeString(launchActivity);
		parcel.writeString(versionName);
		parcel.writeString(versionCode);
		parcel.writeLong(firstInstallTime);
		parcel.writeByte((byte) (isSystemApp ? 1 : 0));
	}
}