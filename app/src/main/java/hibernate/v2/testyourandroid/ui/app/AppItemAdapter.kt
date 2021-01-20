package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.databinding.ListItemInfoAppBinding
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
        return ItemViewHolder(
            ListItemInfoAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val itemBinding = (rawHolder as ItemViewHolder).viewBinding

        itemBinding.titleTv.text = item.appName
        itemBinding.contentTv.text = item.packageName
        Glide.with(itemBinding.iconIv.context)
            .load(item.icon)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(itemBinding.iconIv)
        itemBinding.systemAppIndicator.visibility =
            if (item.isSystemApp) View.VISIBLE else View.GONE
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { view -> itemClickListener.onItemDetailClick(view.tag as AppItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(val viewBinding: ListItemInfoAppBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}