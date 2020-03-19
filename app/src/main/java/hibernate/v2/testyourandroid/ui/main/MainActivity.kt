package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.TransactionDetails
import com.blankj.utilcode.util.AppUtils
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.helper.UtilHelper.iapProductIdList
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*
import java.util.ArrayList

class MainActivity : BaseActivity(), RatingDialogListener {
    private lateinit var preferences: SharedPreferences
    private lateinit var defaultPreferences: SharedPreferences
    private lateinit var billingProcessor: BillingProcessor

    private val productNameArray =
        arrayOf("Buy Me A Orange Juice", "Buy Me A Coffee", "Buy Me A Big Mac")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_container)
        preferences = getSharedPreferences(UtilHelper.PREF, 0)
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setSupportActionBar(toolbar)
        initActionBar(toolbar, titleId = R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_IAP_KEY,
            object : IBillingHandler {
                override fun onProductPurchased(productId: String, details: TransactionDetails?) {
                    if (iapProductIdList().contains(productId)) {
                        defaultPreferences.edit().putBoolean(UtilHelper.PREF_IAP, true).apply()
                        MaterialDialog(this@MainActivity)
                            .title(R.string.iab_complete_title)
                            .customView(R.layout.dialog_donate)
                            .positiveButton(R.string.ui_okay)
                            .show()
                    }
                }

                override fun onPurchaseHistoryRestored() {}
                override fun onBillingError(errorCode: Int, error: Throwable?) {}
                override fun onBillingInitialized() {}
            })

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()

        countRate()
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_language -> openDialogLanguage()
            R.id.action_iap -> checkPayment()
        }
        return super.onOptionsItemSelected(item)
    }

    fun checkPayment() {
        val isAvailable = BillingProcessor.isIabServiceAvailable(this)
        if (isAvailable) {
            openDialogIAP()
        } else {
            Toast.makeText(this, R.string.ui_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun openDialogRate() {
        AppRatingDialog.Builder()
            .setPositiveButtonText(R.string.rate_posbtn)
            .setNegativeButtonText(R.string.rate_navbtn)
            .setNeutralButtonText(R.string.rate_netbtn)
            .setNumberOfStars(5)
            .setDefaultRating(5)
            .setTitle(R.string.rate_title)
            .setDescription(R.string.rate_message)
            .setCommentInputEnabled(false)
            .setStarColor(R.color.gold)
            .setTitleTextColor(R.color.white)
            .setDescriptionTextColor(R.color.grey200)
            .setWindowAnimation(R.style.RatingDialogFadeAnimation)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@MainActivity)
            .show()
    }

    private fun countRate() {
        var countRate = preferences.getInt(UtilHelper.PREF_COUNT_RATE, 0)
        if (countRate == 5) {
            openDialogRate()
        }
        countRate++
        preferences.edit().putInt(UtilHelper.PREF_COUNT_RATE, countRate).apply()
    }

    fun openDialogLanguage() {
        MaterialDialog(this)
            .title(R.string.title_activity_language)
            .listItemsSingleChoice(
                R.array.language_choose,
                waitForPositiveButton = false
            ) { dialog, index, _ ->
                dialog.dismiss()
                val editor = defaultPreferences.edit()
                val languageLocaleCodeArray = resources.getStringArray(R.array.language_locale_code)
                val languageLocaleCountryCodeArray =
                    resources.getStringArray(R.array.language_locale_country_code)
                editor
                    .putString(UtilHelper.PREF_LANGUAGE, languageLocaleCodeArray[index])
                    .putString(
                        UtilHelper.PREF_LANGUAGE_COUNTRY,
                        languageLocaleCountryCodeArray[index]
                    )
                    .apply()

                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.let {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .negativeButton(R.string.ui_cancel)
            .show()
    }

    private fun openDialogIAP() {
        MaterialDialog(this)
            .title(R.string.title_activity_test_ad_remover)
            .listItemsSingleChoice(
                items = productNameArray.toCollection(ArrayList()),
                waitForPositiveButton = false
            ) { dialog, index, _ ->
                billingProcessor.purchase(this, iapProductIdList()[index])
                dialog.dismiss()
            }
            .negativeButton(R.string.ui_cancel)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNegativeButtonClicked() {
        preferences.edit().putInt(UtilHelper.PREF_COUNT_RATE, 1000).apply()
    }

    override fun onNeutralButtonClicked() {
        preferences.edit().putInt(UtilHelper.PREF_COUNT_RATE, 0).apply()
    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        preferences.edit().putInt(UtilHelper.PREF_COUNT_RATE, 1000).apply()
        if (rate >= 4) {
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                intent.data = Uri.parse("market://details?id=hibernate.v2.testyourandroid")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                intent.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                startActivity(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_SEND)
            var text = "Android Version: " + Build.VERSION.RELEASE + "\n"
            text += "SDK Level: " + Build.VERSION.SDK_INT + "\n"
            text += "Version: " + AppUtils.getAppVersionName() + "\n"
            text += "Brand: " + Build.BRAND + "\n"
            text += "Model: " + Build.MODEL + "\n\n\n"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.CONTACT_EMAIL))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)
        }
    }
}