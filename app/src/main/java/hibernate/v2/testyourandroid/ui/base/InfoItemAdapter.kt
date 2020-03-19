package hibernate.v2.testyourandroid.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoItem

/**
 * Created by himphen on 25/5/16.
 */
class InfoItemAdapter(
    private val list: List<InfoItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SIMPLE = 1
        const val TYPE_MINIMIZED = 2
        const val TYPE_SINGLE_LINE = 3
    }

    var type = TYPE_SIMPLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            TYPE_SIMPLE -> ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_info, parent, false)
            )
            TYPE_SINGLE_LINE -> SingleLineItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_info_single_line, parent, false)
            )
            else -> ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_info_minimized, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ItemViewHolder) {
            holder.titleTv.text = item.titleText
            holder.contentTv.text = item.contentText
        } else if (holder is SingleLineItemViewHolder) {
            holder.titleTv.text = item.titleText
        }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTv: TextView = view.findViewById(R.id.text1)
        var contentTv: TextView = view.findViewById(R.id.text2)
    }

    internal class SingleLineItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTv: TextView = view.findViewById(R.id.text1)
    }

}