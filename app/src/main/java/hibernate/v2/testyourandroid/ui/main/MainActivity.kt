package hibernate.v2.testyourandroid.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.himphen.logger.Logger
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.core.SharedPreferencesManager
import hibernate.v2.testyourandroid.databinding.ActivityContainerBinding
import hibernate.v2.testyourandroid.ui.base.BaseActivity
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.iapProductIdList
import hibernate.v2.testyourandroid.util.Utils.iapProductIdListAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity<ActivityContainerBinding>() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private lateinit var billingClient: BillingClient

    private var skuDetailsList: List<SkuDetails>? = null
    private var mInterstitialAd: InterstitialAd? = null

    private var countInterstitialAd = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        initActionBar(viewBinding.toolbar.root, titleId = R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)

        setupBillingClient()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()

        initInterstitialAd()
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

    override fun onResume() {
        super.onResume()

        countInterstitialAd()
    }

    private fun initInterstitialAd() {
        if (Utils.isAdHidden()) {
            return
        }
        InterstitialAd.load(
            this,
            BuildConfig.ADMOB_FULL_SCREEN_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Logger.d("Ad failed to show." + adError?.message)
                            }

                            override fun onAdShowedFullScreenContent() {
                                initInterstitialAd()
                            }
                        }
                }
            }
        )
    }

    private fun countInterstitialAd() {
        if (Utils.isAdHidden()) {
            return
        }

        if (++countInterstitialAd == 3) {
            mInterstitialAd?.show(this)
        }
    }

    fun openDialogLanguage() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_activity_language)
            .setItems(R.array.language_choose) { dialog, index ->
                dialog.dismiss()
                val languageLocaleCodeArray = resources.getStringArray(R.array.language_locale_code)
                val languageLocaleCountryCodeArray =
                    resources.getStringArray(R.array.language_locale_country_code)
                sharedPreferencesManager.language = languageLocaleCodeArray[index]
                sharedPreferencesManager.languageCountry = languageLocaleCountryCodeArray[index]

                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.let {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .setNegativeButton(R.string.ui_cancel, null)
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

            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_activity_test_ad_remover)
                .setItems(skuTitleList.toTypedArray()) { dialog, index ->
                    val billingFlowParams = BillingFlowParams
                        .newBuilder()
                        .setSkuDetails(skuDetailsList[index])
                        .build()
                    billingClient.launchBillingFlow(this, billingFlowParams)

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.ui_cancel, null)
                .show()
        } ?: run {
            Toast.makeText(this@MainActivity, R.string.ui_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener { billingResult, purchases ->
                lifecycleScope.launch {
                    Logger.d(billingResult.responseCode)
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            onHandlePurchase(purchase)
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        onPurchased()
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, R.string.ui_error, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
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

                    lifecycleScope.launch {
                        skuDetailsList = querySkuDetails(params)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private suspend fun onHandlePurchase(purchase: Purchase) {
        Logger.d(purchase.purchaseState)

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (purchase.isAcknowledged) {
                onPurchased()
            } else {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                if (acknowledgePurchase(acknowledgePurchaseParams)) {
                    onPurchased()
                }
            }
        }
    }

    private suspend fun onPurchased() {
        sharedPreferencesManager.iap = true
        if (isFinishing) return

        withContext(Dispatchers.Main) {
            MaterialAlertDialogBuilder(this@MainActivity)
                .setView(R.layout.dialog_donate)
                .setPositiveButton(R.string.ui_okay, null)
                .show()
        }
    }

    fun checkPurchaseHistory() {
        lifecycleScope.launch {
            iapProductIdListAll().forEach {
                if (queryPurchaseHistory(it)) {
                    onPurchased()
                    return@forEach
                }
            }
        }
    }

    override fun getActivityViewBinding(): ActivityContainerBinding =
        ActivityContainerBinding.inflate(layoutInflater)

    private suspend fun querySkuDetails(params: SkuDetailsParams): List<SkuDetails>? {
        return suspendCoroutine { continuation ->
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(skuDetailsList)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

    private suspend fun queryPurchaseHistory(iapId: String): Boolean {
        return suspendCoroutine { continuation ->
            billingClient.queryPurchaseHistoryAsync(iapId) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(params: AcknowledgePurchaseParams): Boolean {
        return suspendCoroutine { continuation ->
            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }
}