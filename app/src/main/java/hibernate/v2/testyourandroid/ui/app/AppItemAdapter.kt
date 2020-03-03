package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.AppItem

/**
 * Created by himphen on 25/5/16.
 */
class AppItemAdapter(
        private val list: List<AppItem>,
        private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(appItem: AppItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_info_app, parent, false))
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val holder = rawHolder as ItemViewHolder
        holder.titleTv.text = item.appName
        holder.contentTv.text = item.packageName
        Glide.with(holder.iconIv.context)
                .load(item.icon)
                .apply(RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.iconIv)
        holder.systemAppIndicator.visibility = if (item.isSystemApp) View.VISIBLE else View.GONE
        holder.rootView.tag = item
        holder.rootView.setOnClickListener { view -> itemClickListener.onItemDetailClick(view.tag as AppItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTv: TextView = view.findViewById(R.id.text1)
        var contentTv: TextView = view.findViewById(R.id.text2)
        var iconIv: ImageView = view.findViewById(R.id.icon)
        var rootView: LinearLayout = view.findViewById(R.id.root_view)
        var systemAppIndicator: FrameLayout = view.findViewById(R.id.systemAppIndicator)
    }

}