package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
class GridItem {
    var mainText: String
    var mainImageId: Int
    var intentClass: Class<*>? = null
    var actionType: String? = null

    constructor(mainText: String, mainImageId: Int, intentClass: Class<*>?) {
        this.mainText = mainText
        this.mainImageId = mainImageId
        this.intentClass = intentClass
    }

    constructor(mainText: String, mainImageId: Int, actionType: String?) {
        this.mainText = mainText
        this.mainImageId = mainImageId
        this.actionType = actionType
    }
}