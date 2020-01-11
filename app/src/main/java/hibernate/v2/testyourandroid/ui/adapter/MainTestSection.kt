package hibernate.v2.testyourandroid.ui.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.appbrain.AppBrain
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.activity.MainActivity
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters


class MainTestSection constructor(
        private val context: Context,
        private val title: String,
        private val gridItemList: List<GridItem>
) : Section(SectionParameters.builder()
        .itemResourceId(R.layout.item_main_icon)
        .headerResourceId(R.layout.item_main_header)
        .build()) {
    override fun getContentItemsTotal(): Int {
        return gridItemList.size
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = gridItemList[position]
        val itemViewHolder = holder as ItemViewHolder
        Glide.with(itemViewHolder.mainIv.context)
                .load(item.mainImageId)
                .apply(RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(itemViewHolder.mainIv)
        itemViewHolder.mainTv.text = item.mainText
        itemViewHolder.rootView.tag = item
        itemViewHolder.rootView.setOnClickListener { v ->
            val gridItem = v.tag as GridItem
            gridItem.intentClass?.let {
                val intent = Intent().setClass(context, it)
                context.startActivity(intent)
            } ?: run {
                when (gridItem.actionType) {
                    "donate" -> (context as MainActivity).checkPayment()
                    "language" -> (context as MainActivity).openDialogLanguage()
                    "rate" -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        try {
                            intent.data = Uri.parse("market://details?id=hibernate.v2.testyourandroid")
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                            context.startActivity(intent)
                        }
                    }
                    "app_brain" -> {
                        val ads = AppBrain.getAds()
                        ads.setOfferWallClickListener(context, v)
                    }
                }
            }
        }
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.headerTv.text = title
    }

    internal class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTv: AppCompatTextView = itemView.findViewById(R.id.headerTv)
    }

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: LinearLayout = view.findViewById(R.id.root_view)
        val mainTv: AppCompatTextView = view.findViewById(R.id.mainTv)
        val mainIv: ImageView = view.findViewById(R.id.mainIv)
    }
}
