package hibernate.v2.testyourandroid.model

/**
 * Created by himphen on 24/5/16.
 */
class GridItem(
    var text: String,
    var image: Int,
    var badge: Badge = Badge.NONE,
    var intentClass: Class<*>? = null,
    var action: Action? = null
) {
    enum class Badge {
        NONE,
        NEW,
        BETA,
    }

    enum class Action {
        HOME_RATE,
        HOME_LANGUAGE,
        HOME_DONATE,
        HOME_APP_BRAIN,
        APP_INFO_OPEN,
        APP_INFO_UNINSTALL,
        APP_INFO_SETTINGS,
        APP_INFO_PLAY_STORE,
    }
}