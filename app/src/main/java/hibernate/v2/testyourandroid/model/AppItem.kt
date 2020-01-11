package hibernate.v2.testyourandroid.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by himphen on 24/5/16.
 */
class AppItem : Parcelable {
    var appName: String? = null
    var packageName: String? = null
    var sourceDir: String? = null
    private var publicSourceDir: String? = null
    private var launchActivity: String? = null
    var versionName: String? = null
    var versionCode: String? = null
    var firstInstallTime: Long = 0
    var icon: Drawable? = null
    var isSystemApp = false

    constructor()

    constructor(parcel: Parcel) {
        appName = parcel.readString()
        packageName = parcel.readString()
        sourceDir = parcel.readString()
        publicSourceDir = parcel.readString()
        launchActivity = parcel.readString()
        versionName = parcel.readString()
        versionCode = parcel.readString()
        firstInstallTime = parcel.readLong()
        isSystemApp = parcel.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(appName)
        parcel.writeString(packageName)
        parcel.writeString(sourceDir)
        parcel.writeString(publicSourceDir)
        parcel.writeString(launchActivity)
        parcel.writeString(versionName)
        parcel.writeString(versionCode)
        parcel.writeLong(firstInstallTime)
        parcel.writeByte((if (isSystemApp) 1 else 0).toByte())
    }

    companion object CREATOR : Parcelable.Creator<AppItem> {
        override fun createFromParcel(parcel: Parcel): AppItem {
            return AppItem(parcel)
        }

        override fun newArray(size: Int): Array<AppItem?> {
            return arrayOfNulls(size)
        }
    }
}