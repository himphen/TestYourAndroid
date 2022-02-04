package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import hibernate.v2.testyourandroid.databinding.ItemListInfoAppBinding
import hibernate.v2.testyourandroid.model.AppItem

/**
 * Created by himphen on 25/5/16.
 */
class AppItemAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<AppItem, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<AppItem>() {
        override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
            return oldItem.packageName == newItem.packageName
        }
    }

    interface ItemClickListener {
        fun onItemDetailClick(appItem: AppItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ItemListInfoAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemBinding = (rawHolder as ItemViewHolder).viewBinding

        itemBinding.titleTv.text = item.appName
        itemBinding.contentTv.text = item.packageName
        itemBinding.iconIv.load(item.icon)
        itemBinding.systemAppIndicator.visibility =
            if (item.isSystemApp) View.VISIBLE else View.GONE
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { view -> itemClickListener.onItemDetailClick(view.tag as AppItem) }
    }

    internal class ItemViewHolder(val viewBinding: ItemListInfoAppBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}
