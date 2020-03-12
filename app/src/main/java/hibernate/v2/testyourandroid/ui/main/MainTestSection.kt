package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.nekocode.badge.BadgeDrawable
import com.appbrain.AppBrain
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.GridItem
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters


class MainTestSection constructor(
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
        holder as ItemViewHolder
        Glide.with(holder.mainIv.context)
                .load(item.mainImageId)
                .apply(RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.mainIv)
        holder.mainTv.text = item.mainText

        when (item.badge) {
            GridItem.Badge.NEW -> {
                val drawable = BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(ContextCompat.getColor(holder.mainIv.context, R.color.green500))
                        .text1("NEW")
                        .build()

                holder.badgeTv.text = SpannableString(drawable.toSpannable())
                holder.badgeTv.visibility = View.VISIBLE
            }
            GridItem.Badge.BETA -> {
                val drawable = BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(ContextCompat.getColor(holder.mainIv.context, R.color.gold))
                        .text1("BETA")
                        .build()

                holder.badgeTv.text = SpannableString(drawable.toSpannable())
                holder.badgeTv.visibility = View.VISIBLE
            }
            GridItem.Badge.NONE -> {
                holder.badgeTv.visibility = View.GONE
            }
        }

        holder.rootView.tag = item
        holder.rootView.setOnClickListener { v ->
            val gridItem = v.tag as GridItem
            gridItem.intentClass?.let {
                val intent = Intent().setClass(v.context, it)
                v.context.startActivity(intent)
            } ?: run {
                when (gridItem.actionType) {
                    "donate" -> (v.context as MainActivity).checkPayment()
                    "language" -> (v.context as MainActivity).openDialogLanguage()
                    "rate" -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        try {
                            intent.data = Uri.parse("market://details?id=hibernate.v2.testyourandroid")
                            v.context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                            v.context.startActivity(intent)
                        }
                    }
                    "app_brain" -> {
                        val ads = AppBrain.getAds()
                        ads.setOfferWallClickListener(v.context, v)
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
        var badgeTv: TextView = view.findViewById(R.id.badgeTv)
    }
}
