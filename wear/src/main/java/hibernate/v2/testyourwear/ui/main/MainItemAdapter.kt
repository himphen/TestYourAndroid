package hibernate.v2.testyourwear.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import hibernate.v2.testyourwear.R
import hibernate.v2.testyourwear.model.MainItem

/**
 * Created by himphen on 25/5/16.
 */
class MainItemAdapter(
    private val list: List<MainItem>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(item: MainItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        holder as ItemViewHolder
        holder.mainIv.load(item.mainImageId)
        holder.mainTv.text = item.mainText
        holder.rootView.tag = item
        holder.rootView.setOnClickListener { v -> listener.onItemDetailClick(v.tag as MainItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootView: LinearLayout = itemView.findViewById(R.id.root_view)
        val mainIv: ImageView = itemView.findViewById(R.id.mainIv)
        val mainTv: TextView = itemView.findViewById(R.id.mainTv)
    }
}