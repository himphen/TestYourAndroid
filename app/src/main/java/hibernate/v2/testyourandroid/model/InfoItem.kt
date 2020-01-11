package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
open class InfoItem {
    var titleText: String? = null
    var contentText: String? = null

    constructor()
    constructor(titleText: String?, contentText: String?) {
        this.titleText = titleText
        this.contentText = contentText
    }

}