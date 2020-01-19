package hibernate.v2.testyourandroid.ui.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.InfoHeader
import hibernate.v2.testyourandroid.model.InfoItem

/**
 * Created by himphen on 25/5/16.
 */
class InfoItemAdaptor(
        private val dataList: List<InfoItem>
) : HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, InfoHeader?, InfoItem?, RecyclerView.ViewHolder?>() {

    public override fun onCreateHeaderViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_item_info, parent, false))
    }

    public override fun onBindHeaderViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val header = header
        val holder = rawHolder as HeaderViewHolder
        holder.titleTv.text = header?.titleText
    }

    public override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_info, parent, false)
        return ItemViewHolder(itemView)
    }

    public override fun onBindItemViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        // Due to adding header, we need to position -1
        val item = dataList[position - 1]
        val holder = rawHolder as ItemViewHolder
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
        val titleTv: TextView = view.findViewById(R.id.text1)
        val contentTv: TextView = view.findViewById(R.id.text2)
    }

}