package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
data class AppChooseItem(
    override var titleText: String?,
    override var contentText: String?,
    val appType: Int
) : BaseInfoItem()
