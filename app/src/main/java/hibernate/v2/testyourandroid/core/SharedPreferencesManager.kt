package hibernate.v2.testyourandroid.core


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import hibernate.v2.testyourandroid.util.PreferenceUtils

class SharedPreferencesManager(val context: Context) {

    companion object {
        const val PREF_IAP = "iap"
        const val PREF_LANGUAGE = "PREF_LANGUAGE"
        const val PREF_LANGUAGE_COUNTRY = "PREF_LANGUAGE_COUNTRY"
        const val PREF_COUNT_RATE = "PREF_COUNT_RATE"
        const val PREF_THEME = "PREF_THEME"
        const val PREF_UNIT = "pref_unit"
    }

    private val preferences: SharedPreferences = PreferenceUtils.sharedPrefs(context)

    var iap: Boolean
        get() = preferences.getBoolean(PREF_IAP, false)
        set(value) {
            preferences.edit {
                putBoolean(PREF_IAP, value)
                apply()
            }
        }

    var language: String
        get() = preferences.getString(PREF_LANGUAGE, "") ?: ""
        set(value) {
            preferences.edit {
                putString(PREF_LANGUAGE, value)
                apply()
            }
        }

    var languageCountry: String
        get() = preferences.getString(PREF_LANGUAGE_COUNTRY, "") ?: ""
        set(value) {
            preferences.edit {
                putString(PREF_LANGUAGE_COUNTRY, value)
                apply()
            }
        }

    var countRate: Int
        get() = preferences.getInt(PREF_COUNT_RATE, 0)
        set(value) {
            preferences.edit {
                putInt(PREF_COUNT_RATE, value)
                apply()
            }
        }

    var theme: String
        get() = preferences.getString(PREF_THEME, "") ?: ""
        set(value) {
            preferences.edit {
                putString(PREF_THEME, value)
                apply()
            }
        }

    var unit: Boolean
        get() = preferences.getBoolean(PREF_UNIT, true)
        set(value) {
            preferences.edit {
                putBoolean(PREF_UNIT, value)
                apply()
            }
        }
}