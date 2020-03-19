package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
class GridItem {
    var mainText: String
    var mainImageId: Int
    var intentClass: Class<*>? = null
    var actionType: String? = null
    var badge: Badge = Badge.NONE

    constructor(
        mainText: String,
        mainImageId: Int,
        intentClass: Class<*>?,
        badge: Badge = Badge.NONE
    ) {
        this.mainText = mainText
        this.mainImageId = mainImageId
        this.intentClass = intentClass
        this.badge = badge
    }

    constructor(
        mainText: String,
        mainImageId: Int,
        actionType: String?,
        badge: Badge = Badge.NONE
    ) {
        this.mainText = mainText
        this.mainImageId = mainImageId
        this.actionType = actionType
        this.badge = badge
    }

    enum class Badge(var type: Int) {
        NONE(0),
        NEW(1),
        BETA(2),
    }
}