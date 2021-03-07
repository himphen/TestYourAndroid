package hibernate.v2.testyourandroid.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ScreenUtils
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hibernate.v2.testyourandroid.databinding.FragmentMainGridviewBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.main.item.MainTestAdItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestTitleItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestUtils
import hibernate.v2.testyourandroid.util.Utils.isAdHidden

class MainTestFragment : BaseFragment<FragmentMainGridviewBinding>() {

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

        addTestSectionItem()
        if (!isAdHidden()) loadBannerAd(0)

        val columnCount = if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) 4 else 3
        val gridLayoutManager = GridLayoutManager(activity, columnCount)
        gridLayoutManager.spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (list[position]) {
                        is MainTestAdItem,
                        is MainTestTitleItem -> columnCount
                        else -> 1
                    }
                }
            }
        viewBinding!!.gridRv.setHasFixedSize(true)
        viewBinding!!.gridRv.layoutManager = gridLayoutManager
        adapter = MainTestAdapter(list)
        viewBinding!!.gridRv.adapter = adapter
    }

    private fun addTestSectionItem() {
        val utils = MainTestUtils(requireContext())
        list.addAll(utils.tools())
        list.addAll(utils.info())
        utils.addAdItem()?.let { list.add(it) }
        list.addAll(utils.hardware())
        list.addAll(utils.sensor())
        utils.addAdItem()?.let { list.add(it) }
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

            override fun onAdFailedToLoad(errorCode: Int) {
                FirebaseCrashlytics.getInstance()
                    .log("Home Adview onAdFailedToLoad errorCode: $errorCode")
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