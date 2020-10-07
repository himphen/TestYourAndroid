package hibernate.v2.testyourandroid.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by himphen on 24/5/16.
 */
@Parcelize
data class AppItem(
    var appName: String,
    var packageName: String,
    var isSystemApp: Boolean,
) : Parcelable {
    @IgnoredOnParcel
    var icon: Drawable? = null
}