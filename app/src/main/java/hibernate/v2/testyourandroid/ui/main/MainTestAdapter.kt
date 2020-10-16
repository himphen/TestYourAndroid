package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import hibernate.v2.testyourandroid.ui.main.item.MainTestAdItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestTitleItem

class MainTestAdapter(private val list: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_AD_VIEW = 1
        const val VIEW_TYPE_ICON = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> TitleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_section_title, parent, false)
            )
            VIEW_TYPE_AD_VIEW -> AdViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_section_ad, parent, false)
            )
            else -> IconViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main_section_icon, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is GridItem -> VIEW_TYPE_ICON
            is MainTestTitleItem -> VIEW_TYPE_TITLE
            else -> VIEW_TYPE_AD_VIEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IconViewHolder -> {
                val item = list[position] as GridItem
                Glide.with(holder.mainIv.context)
                    .load(item.image)
                    .apply(
                        RequestOptions()
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(holder.mainIv)
                holder.mainTv.text = item.text

                when (item.badge) {
                    GridItem.Badge.NEW -> {
                        val drawable = BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(
                                ContextCompat.getColor(
                                    holder.mainIv.context,
                                    R.color.green500
                                )
                            )
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
                        when (gridItem.action) {
                            GridItem.Action.HOME_DONATE -> (v.context as MainActivity).openDialogIAP()
                            GridItem.Action.HOME_LANGUAGE -> (v.context as MainActivity).openDialogLanguage()
                            GridItem.Action.HOME_RATE -> {
                                val intent = Intent(Intent.ACTION_VIEW)
                                try {
                                    intent.data =
                                        Uri.parse("market://details?id=hibernate.v2.testyourandroid")
                                    v.context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    intent.data =
                                        Uri.parse("https://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid")
                                    v.context.startActivity(intent)
                                }
                            }
                            GridItem.Action.HOME_APP_BRAIN -> {
                                val ads = AppBrain.getAds()
                                ads.setOfferWallClickListener(v.context, v)
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            is TitleViewHolder -> {
                val item = list[position] as MainTestTitleItem
                holder.headerTv.text = item.title
            }
            is AdViewHolder -> {
                val item = list[position] as MainTestAdItem
                if (holder.rootView.childCount > 0) {
                    holder.rootView.removeAllViews()
                }
                (item.adView.parent as? ViewGroup)?.removeView(item.adView)
                holder.rootView.addView(item.adView)
            }
        }
    }

    internal class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTv: AppCompatTextView = itemView.findViewById(R.id.titleTv)
    }

    internal class IconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: LinearLayout = view.findViewById(R.id.root_view)
        val mainTv: AppCompatTextView = view.findViewById(R.id.mainTv)
        val mainIv: ImageView = view.findViewById(R.id.mainIv)
        val badgeTv: TextView = view.findViewById(R.id.badgeTv)
    }

    internal class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: RelativeLayout = view.findViewById(R.id.root_view)
    }
}