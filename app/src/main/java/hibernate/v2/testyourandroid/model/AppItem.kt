package hibernate.v2.testyourandroid.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Created by himphen on 24/5/16.
 */
@Parcelize
data class AppItem(
    var appName: String? = null,
    var packageName: String? = null,
    var sourceDir: String? = null,
    private var publicSourceDir: String? = null,
    private var launchActivity: String? = null,
    var versionName: String? = null,
    var versionCode: String? = null,
    var firstInstallTime: Long = 0,
    var icon: @RawValue Drawable? = null,
    var isSystemApp: Boolean = false,
) : Parcelable