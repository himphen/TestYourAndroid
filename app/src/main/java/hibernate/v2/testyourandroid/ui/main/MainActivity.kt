package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.orhanobut.logger.Logger
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityContainerBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.iapProductIdList
import hibernate.v2.testyourandroid.util.Utils.iapProductIdListAll
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : BaseActivity<ActivityContainerBinding>(), RatingDialogListener {

    private lateinit var defaultPreferences: SharedPreferences
    private lateinit var billingClient: BillingClient

    private var skuDetailsList: List<SkuDetails>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        initActionBar(viewBinding.toolbar.root, titleId = R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)

        setupBillingClient()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()

        countRate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                Logger.d("// Night mode is not active, we're using the light theme")
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                Logger.d("// Night mode is active, we're using dark theme")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_language -> openDialogLanguage()
            R.id.action_iap -> openDialogIAP()
        }
        return super.onOptionsItemSelected(item)
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
            .setStarColor(R.color.lineColor2)
            .setTitleTextColor(R.color.white)
            .setDescriptionTextColor(R.color.ratingText)
            .setWindowAnimation(R.style.RatingDialogFadeAnimation)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this@MainActivity)
            .show()
    }

    private fun countRate() {
        var countRate = defaultPreferences.getInt(Utils.PREF_COUNT_RATE, 0)
        if (countRate == 5) {
            openDialogRate()
        }
        countRate++
        defaultPreferences.edit().putInt(Utils.PREF_COUNT_RATE, countRate).apply()
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
                    .putString(Utils.PREF_LANGUAGE, languageLocaleCodeArray[index])
                    .putString(
                        Utils.PREF_LANGUAGE_COUNTRY,
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

    fun openDialogIAP() {
        skuDetailsList?.let { skuDetailsList ->
            if (skuDetailsList.isNullOrEmpty()) {
                Toast.makeText(this, R.string.ui_error, Toast.LENGTH_LONG).show()
                return
            }

            val skuTitleList = skuDetailsList.map { skuDetails ->
                val p: Pattern = Pattern.compile("(?> \\(.+?\\))$", Pattern.CASE_INSENSITIVE)
                val m: Matcher = p.matcher(skuDetails.title)
                val titleWithoutAppName: String = m.replaceAll("")

                "${skuDetails.price} - $titleWithoutAppName"
            }

            MaterialDialog(this)
                .title(R.string.title_activity_test_ad_remover)
                .listItemsSingleChoice(
                    items = skuTitleList,
                    waitForPositiveButton = false
                ) { dialog, index, _ ->
                    val billingFlowParams = BillingFlowParams
                        .newBuilder()
                        .setSkuDetails(skuDetailsList[index])
                        .build()
                    billingClient.launchBillingFlow(this, billingFlowParams)

                    dialog.dismiss()
                }
                .negativeButton(R.string.ui_cancel)
                .show()
        } ?: run {
            Toast.makeText(this@MainActivity, R.string.ui_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener { billingResult, purchases ->
                Logger.d(billingResult.responseCode)
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        onHandlePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    onPurchased()
                } else {
                    Toast.makeText(this@MainActivity, R.string.ui_error, Toast.LENGTH_LONG).show()
                }
            }
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    val params = SkuDetailsParams
                        .newBuilder()
                        .setSkusList(iapProductIdList())
                        .setType(BillingClient.SkuType.INAPP)
                        .build()
                    billingClient.querySkuDetailsAsync(params) { billingResult2, skuDetailsList ->
                        if (billingResult2.responseCode == BillingClient.BillingResponseCode.OK) {
                            this@MainActivity.skuDetailsList = skuDetailsList
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun onHandlePurchase(purchase: Purchase) {
        Logger.d(purchase.purchaseState)

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)

                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                    onPurchased()
                }
            } else {
                onPurchased()
            }
        }
    }

    private fun onPurchased() {
        defaultPreferences.edit().putBoolean(Utils.PREF_IAP, true).apply()
        MaterialDialog(this)
            .customView(R.layout.dialog_donate)
            .positiveButton(R.string.ui_okay)
            .show()
    }

    fun checkPurchaseHistory() {
        iapProductIdListAll().forEach {
            billingClient.queryPurchaseHistoryAsync(it) { _, _ ->
                defaultPreferences.edit().putBoolean(Utils.PREF_IAP, true).apply()
                onPurchased()
            }
        }
    }

    // RatingDialogListener
    override fun onNegativeButtonClicked() {
        defaultPreferences.edit().putInt(Utils.PREF_COUNT_RATE, 1000).apply()
    }

    override fun onNeutralButtonClicked() {
        defaultPreferences.edit().putInt(Utils.PREF_COUNT_RATE, 0).apply()
    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        defaultPreferences.edit().putInt(Utils.PREF_COUNT_RATE, 1000).apply()
        if (rate >= 4) {
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                intent.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
            }
        }
    }

    override fun getActivityViewBinding(): ActivityContainerBinding =
        ActivityContainerBinding.inflate(layoutInflater)
}