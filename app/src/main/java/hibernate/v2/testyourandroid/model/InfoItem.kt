package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
data class InfoItem(
    var titleText: String,
    var contentText: String?,
    private var id: Int? = null
) {
    fun getId() = id ?: hashCode()
}
