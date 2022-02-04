package hibernate.v2.testyourandroid.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.databinding.ItemListInfoBinding
import hibernate.v2.testyourandroid.databinding.ItemListInfoMinimizedBinding
import hibernate.v2.testyourandroid.databinding.ItemListInfoSingleLineBinding
import hibernate.v2.testyourandroid.model.InfoItem

/**
 * Created by himphen on 25/5/16.
 */
class InfoItemAdapter : ListAdapter<InfoItem, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<InfoItem>() {
        override fun areItemsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
            return oldItem.contentText == newItem.contentText
        }
    }

    companion object {
        const val TYPE_SIMPLE = 1
        const val TYPE_MINIMIZED = 2
        const val TYPE_SINGLE_LINE = 3
    }

    var type = TYPE_SIMPLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            TYPE_SIMPLE -> ItemViewHolder(
                ItemListInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_SINGLE_LINE -> SingleLineItemViewHolder(
                ItemListInfoSingleLineBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> MinimizedLineItemViewHolder(
                ItemListInfoMinimizedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
                itemBinding.contentTv.text = item.contentText
            }
            is SingleLineItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
            }
            is MinimizedLineItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
                itemBinding.contentTv.text = item.contentText
            }
        }
    }

    internal class ItemViewHolder(val viewBinding: ItemListInfoBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class SingleLineItemViewHolder(val viewBinding: ItemListInfoSingleLineBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class MinimizedLineItemViewHolder(val viewBinding: ItemListInfoMinimizedBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}
