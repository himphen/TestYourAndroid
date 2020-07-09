package hibernate.v2.testyourandroid.ui.main

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import hibernate.v2.testyourandroid.R
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters


class MainAdSection(private val adView: AdView) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.item_main_ad)
        .build()
) {
    override fun getContentItemsTotal(): Int {
        return 1
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemViewHolder

        // The AdViewHolder recycled by the RecyclerView may be a different
        // instance than the one used previously for this position. Clear the
        // AdViewHolder of any subviews in case it has a different
        // AdView associated with it, and make sure the AdView for this position doesn't
        // already have a parent of a different recycled AdViewHolder.
        if (holder.rootView.childCount > 0) {
            holder.rootView.removeAllViews()
        }
        (adView.parent as? ViewGroup)?.removeView(adView)

        holder.rootView.addView(adView)
    }

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: RelativeLayout = view.findViewById(R.id.root_view)
    }
}
