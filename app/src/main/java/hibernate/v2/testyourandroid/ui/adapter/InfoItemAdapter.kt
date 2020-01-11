package hibernate.v2.testyourandroid.ui.adapter

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
        private val mDataList: List<InfoItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_info, parent, false))
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val holder = rawHolder as ItemViewHolder
        holder.titleTv.text = item.titleText
        holder.contentTv.text = item.contentText
    }

    override fun getItemCount(): Int = mDataList.size

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTv: TextView = view.findViewById(R.id.text1)
        var contentTv: TextView = view.findViewById(R.id.text2)
    }

}