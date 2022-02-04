package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.databinding.ItemListInfoBinding
import hibernate.v2.testyourandroid.model.AppChooseItem

/**
 * Created by himphen on 25/5/16.
 */
class AppChooseAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<AppChooseItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun submitList(list: List<AppChooseItem>?) {
        super.submitList(if (list != null) ArrayList(list) else null)
    }

    class DiffCallback : DiffUtil.ItemCallback<AppChooseItem>() {
        override fun areItemsTheSame(oldItem: AppChooseItem, newItem: AppChooseItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AppChooseItem, newItem: AppChooseItem): Boolean {
            return oldItem.contentText == newItem.contentText
        }
    }

    interface ItemClickListener {
        fun onItemDetailClick(appChooseItem: AppChooseItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ItemListInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemBinding = (holder as ItemViewHolder).viewBinding

        itemBinding.titleTv.text = item.titleText
        itemBinding.contentTv.text = item.contentText
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as AppChooseItem) }
    }

    internal class ItemViewHolder(val viewBinding: ItemListInfoBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}
