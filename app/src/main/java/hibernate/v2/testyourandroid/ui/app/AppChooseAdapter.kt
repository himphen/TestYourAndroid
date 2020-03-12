package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.AppChooseItem

/**
 * Created by himphen on 25/5/16.
 */
class AppChooseAdapter(
        private val list: List<AppChooseItem>,
        private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(appChooseItem: AppChooseItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_info, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        holder as ItemViewHolder
        holder.titleTv.text = item.titleText
        holder.contentTv.text = item.contentText
        holder.rootView.tag = item
        holder.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as AppChooseItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: LinearLayout = view.findViewById(R.id.root_view)
        val titleTv: TextView = view.findViewById(R.id.text1)
        val contentTv: TextView = view.findViewById(R.id.text2)
    }
}