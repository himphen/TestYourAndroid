package hibernate.v2.testyourandroid.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.databinding.ListItemInfoBinding
import hibernate.v2.testyourandroid.databinding.ListItemInfoMinimizedBinding
import hibernate.v2.testyourandroid.databinding.ListItemInfoSingleLineBinding
import hibernate.v2.testyourandroid.model.InfoItem

/**
 * Created by himphen on 25/5/16.
 */
class InfoItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object {
        const val TYPE_SIMPLE = 1
        const val TYPE_MINIMIZED = 2
        const val TYPE_SINGLE_LINE = 3
    }

    var type = TYPE_SIMPLE
    private var list = listOf<InfoItem>()

    constructor() : super()
    constructor(list: List<InfoItem>) : super() {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            TYPE_SIMPLE -> ItemViewHolder(
                ListItemInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_SINGLE_LINE -> SingleLineItemViewHolder(
                ListItemInfoSingleLineBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> MinimizedLineItemViewHolder(
                ListItemInfoMinimizedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
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

    override fun getItemCount(): Int = list.size

    fun setData(list: List<InfoItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    internal class ItemViewHolder(val viewBinding: ListItemInfoBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class SingleLineItemViewHolder(val viewBinding: ListItemInfoSingleLineBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class MinimizedLineItemViewHolder(val viewBinding: ListItemInfoMinimizedBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}