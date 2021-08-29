package hibernate.v2.testyourandroid.ui.main

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ItemMainSectionAdBinding
import hibernate.v2.testyourandroid.databinding.ItemMainSectionIconBinding
import hibernate.v2.testyourandroid.databinding.ItemMainSectionRatingBinding
import hibernate.v2.testyourandroid.databinding.ItemMainSectionTitleBinding
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestAdItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestRatingItem
import hibernate.v2.testyourandroid.ui.main.item.MainTestTitleItem
import hibernate.v2.testyourandroid.util.ext.slideUp

class MainTestAdapter(
    private val list: List<Any>,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(gridItem: GridItem)
        fun onRatingSubmitClick()
    }

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_AD_VIEW = 1
        const val VIEW_TYPE_ICON = 2
        const val VIEW_TYPE_RATING = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> TitleViewHolder(
                ItemMainSectionTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_AD_VIEW -> AdViewHolder(
                ItemMainSectionAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_RATING -> RatingViewHolder(
                ItemMainSectionRatingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> IconViewHolder(
                ItemMainSectionIconBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is GridItem -> VIEW_TYPE_ICON
            is MainTestTitleItem -> VIEW_TYPE_TITLE
            is MainTestRatingItem -> VIEW_TYPE_RATING
            else -> VIEW_TYPE_AD_VIEW
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RatingViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.submitBtn.setOnClickListener {
                    val rating = itemBinding.ratingBar.rating
                    Firebase.analytics.logEvent("home_rating") {
                        param("value", rating.toString())
                    }

                    if (rating >= 4) {
                        itemClickListener.onRatingSubmitClick()
                    }

                    itemBinding.root.slideUp()
                }
                itemBinding.skipBtn.setOnClickListener {
                    itemBinding.root.slideUp()
                }
            }
            is IconViewHolder -> {
                val itemBinding = holder.viewBinding

                val gridItem = list[position] as GridItem
                itemBinding.mainIv.load(gridItem.image)
                itemBinding.mainTv.text = gridItem.text

                when (gridItem.badge) {
                    GridItem.Badge.NEW -> {
                        itemBinding.badgeTv.apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                background.colorFilter = BlendModeColorFilter(
                                    ContextCompat.getColor(
                                        itemBinding.mainIv.context,
                                        R.color.lineColor4
                                    ),
                                    BlendMode.SRC_ATOP
                                )
                            } else {
                                background.setColorFilter(
                                    ContextCompat.getColor(
                                        itemBinding.mainIv.context,
                                        R.color.lineColor4
                                    ), PorterDuff.Mode.SRC_ATOP
                                )
                            }
                            text = "NEW"
                            visibility = View.VISIBLE
                        }
                    }
                    GridItem.Badge.BETA -> {
                        itemBinding.badgeTv.apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                background.colorFilter = BlendModeColorFilter(
                                    ContextCompat.getColor(
                                        itemBinding.mainIv.context,
                                        R.color.lineColor4
                                    ),
                                    BlendMode.SRC_ATOP
                                )
                            } else {
                                background.setColorFilter(
                                    ContextCompat.getColor(
                                        itemBinding.mainIv.context,
                                        R.color.lineColor4
                                    ), PorterDuff.Mode.SRC_ATOP
                                )
                            }
                            text = "BETA"
                            visibility = View.VISIBLE
                        }
                    }
                    GridItem.Badge.NONE -> {
                        itemBinding.badgeTv.visibility = View.GONE
                    }
                }

                itemBinding.rootView.tag = gridItem
                itemBinding.rootView.setOnClickListener { v ->
                    itemClickListener.onItemClick(v.tag as GridItem)
                }
            }
            is TitleViewHolder -> {
                val itemBinding = holder.viewBinding
                val item = list[position] as MainTestTitleItem
                itemBinding.titleTv.text = item.title
            }
            is AdViewHolder -> {
                val itemBinding = holder.viewBinding
                val item = list[position] as MainTestAdItem
                if (itemBinding.rootView.childCount > 0) {
                    itemBinding.rootView.removeAllViews()
                }
                (item.adView.parent as? ViewGroup)?.removeView(item.adView)
                itemBinding.rootView.addView(item.adView)
            }
        }
    }

    internal class TitleViewHolder(val viewBinding: ItemMainSectionTitleBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class IconViewHolder(val viewBinding: ItemMainSectionIconBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class AdViewHolder(val viewBinding: ItemMainSectionAdBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class RatingViewHolder(val viewBinding: ItemMainSectionRatingBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}