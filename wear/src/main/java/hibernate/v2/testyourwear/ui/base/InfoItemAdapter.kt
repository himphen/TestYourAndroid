package hibernate.v2.testyourwear.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.model.InfoHeader
import hibernate.v2.testyourwear.model.InfoItem
import hibernate.v2.testyourwear.util.headderrecyclerview.HeaderRecyclerViewAdapter

/**
 * Created by himphen on 25/5/16.
 */
class InfoItemAdapter(
        private val dataList: List<InfoItem>
) : HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, InfoHeader?, InfoItem?, Void>() {

    override fun onCreateHeaderViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_item_info, parent, false))
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val header = header
        holder as HeaderViewHolder
        holder.titleTv.text = header?.titleText
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_info, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Due to adding header, we need to position -1
        val item = dataList[position - 1]
        holder as ItemViewHolder
        holder.titleTv.text = item.titleText
        holder.contentTv.text = item.contentText
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTv: TextView = view.findViewById(R.id.titleTv)
    }

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTv: TextView = view.findViewById(R.id.titleTv)
        val contentTv: TextView = view.findViewById(R.id.contentTv)
    }
}