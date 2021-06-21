package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hibernate.v2.testyourandroid.core.SharedPreferencesManager
import hibernate.v2.testyourandroid.databinding.FragmentMainGridviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.main.item.MainTestAdItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestRatingItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestTitleItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestUtils
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.isAdHidden
import org.koin.android.ext.android.inject

class MainTestFragment : BaseFragment<FragmentMainGridviewBinding>() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private var countRate = sharedPreferencesManager.countRate

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMainGridviewBinding =
        FragmentMainGridviewBinding.inflate(inflater, container, false)

    private lateinit var adapter: MainTestAdapter
    private var list = mutableListOf<Any>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUpdater(context)
            .showEvery(4)
            .setDisplay(Display.NOTIFICATION)
            .start()

        if (countRate <= 50) {
            sharedPreferencesManager.countRate = ++countRate
        }

        addTestSectionItem()
        if (!isAdHidden()) loadBannerAd(0)

        val columnCount = if (Utils.isTablet() && Utils.isLandscape(context)) 3 else 2
        val gridLayoutManager = GridLayoutManager(activity, columnCount)
        gridLayoutManager.spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (list[position]) {
                        is MainTestAdItem,
                        is MainTestRatingItem,
                        is MainTestTitleItem -> columnCount
                        else -> 1
                    }
                }
            }
        viewBinding!!.gridRv.setHasFixedSize(true)
        viewBinding!!.gridRv.layoutManager = gridLayoutManager
        adapter = MainTestAdapter(list, object : MainTestAdapter.ItemClickListener {
            override fun onRatingSubmitClick() {
                sharedPreferencesManager.countRate = 1000
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                }
            }
        })
        viewBinding!!.gridRv.adapter = adapter
    }

    private fun addTestSectionItem() {
        val utils = MainTestUtils(requireContext())
        list.addAll(utils.tools())
        if (countRate <= 50 && countRate % 5 == 4) {
            list.add(MainTestRatingItem())
        }
        list.addAll(utils.info())
        utils.addAdItem()?.let { list.add(it) }
        list.addAll(utils.hardware())
        list.addAll(utils.sensor())
        utils.addAdItem()?.let { list.add(it) }
        if (countRate <= 50 && countRate % 5 != 4) {
            list.add(MainTestRatingItem())
        }
        list.addAll(utils.other())
    }

    private fun loadBannerAd(index: Int) {
        if (index >= list.size) return

        val item = list[index]
        if (item !is MainTestAdItem) {
            loadBannerAd(index + 1)
            return
        }

        item.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                loadBannerAd(index + 1)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                FirebaseCrashlytics.getInstance()
                    .log("Home Adview onAdFailedToLoad errorCode: ${error.code}")
                loadBannerAd(index + 1)
            }
        }
        item.adView.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.resume()
        }
        super.onResume()
    }

    override fun onPause() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        for (item in list) {
            if (item is MainTestAdItem) item.adView.destroy()
        }
        super.onDestroy()
    }
}