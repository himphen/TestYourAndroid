package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.psdev.licensesdialog.LicensesDialog
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.core.SharedPreferencesManager
import hibernate.v2.testyourandroid.util.Utils
import org.koin.android.ext.android.inject

class MainSettingsFragment : PreferenceFragmentCompat() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>("pref_theme_mode")?.let { preference ->
            preference.summary = getThemeText(sharedPreferencesManager.theme)
            preference.setOnPreferenceClickListener {
                val initialSelection = when (sharedPreferencesManager.theme) {
                    Utils.PrefTheme.THEME_LIGHT.value -> 0
                    Utils.PrefTheme.THEME_DARK.value -> 1
                    else -> 2
                }
                MaterialAlertDialogBuilder(preference.context)
                    .setTitle(getString(R.string.dialog_settings_choose_theme_title))
                    .setSingleChoiceItems(
                        R.array.theme_array,
                        initialSelection
                    ) { dialog, index ->
                        val target = when (index) {
                            0 -> Utils.PrefTheme.THEME_LIGHT
                            1 -> Utils.PrefTheme.THEME_DARK
                            else -> Utils.PrefTheme.THEME_AUTO
                        }

                        sharedPreferencesManager.theme = target.value
                        preference.summary = getThemeText(target.value)

                        Utils.updateTheme(target.value)
                        dialog.dismiss()

                        activity?.recreate()
                    }
                    .setNegativeButton(R.string.ui_cancel, null)
                    .show()
                false
            }
        }

        findPreference<Preference>("pref_more_app")?.setOnPreferenceClickListener {
            try {
                val uri = Uri.parse("market://search?q=pub:\"Hibernate\"")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Utils.notAppFound(activity)
            }

            true
        }

        findPreference<Preference>("pref_improve_translation")?.setOnPreferenceClickListener {
            try {
                val uri = Uri.parse(BuildConfig.CROWDIN_URL)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Utils.notAppFound(activity)
            }

            true
        }

        findPreference<Preference>("pref_share")?.setOnPreferenceClickListener {
            val text =
                getString(R.string.share_message) + "\n\nhttps://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid"
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.share_button)))

            true
        }

        findPreference<Preference>("pref_restore_purchase")?.setOnPreferenceClickListener {
            (activity as? MainActivity)?.checkPurchaseHistory()

            true
        }

        findPreference<Preference>("pref_feedback")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            var text = "Android Version: " + Build.VERSION.RELEASE + "\n"
            text += "SDK Level: " + Build.VERSION.SDK_INT + "\n"
            text += "Version: " + Utils.getAppVersionName(context) + "\n"
            text += "Brand: " + Build.BRAND + "\n"
            text += "Model: " + Build.MODEL + "\n\n\n"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.CONTACT_EMAIL))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)

            true
        }
        findPreference<Preference>("pref_credit")?.setOnPreferenceClickListener {
            LicensesDialog.Builder(context)
                .setNotices(R.raw.notices)
//                .setThemeResourceId(R.style.AppTheme_Dialog_License)
                .build()
                .show()

            true
        }
        findPreference<Preference>("pref_version")?.apply {
            summary = Utils.getAppVersionName(context)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }

    private fun getThemeText(value: String?): String {
        return when (value) {
            Utils.PrefTheme.THEME_LIGHT.value -> getString(R.string.theme_light)
            Utils.PrefTheme.THEME_DARK.value -> getString(R.string.theme_dark)
            else -> getString(R.string.theme_auto)
        }
    }
}