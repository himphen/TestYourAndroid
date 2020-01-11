package hibernate.v2.testyourandroid.ui.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.MainItem

/**
 * Created by himphen on 25/5/16.
 */
class MainItemAdaptor(
        private val mDataList: List<MainItem>,
        private val mListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(item: MainItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_main, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = mDataList[position]
        val holder = rawHolder as ItemViewHolder
        Glide.with(holder.mainIv.context)
                .load(item.mainImageId)
                .apply(RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.mainIv)
        holder.mainTv.text = item.mainText
        holder.rootView.tag = item
        holder.rootView.setOnClickListener { v -> mListener.onItemDetailClick(v.tag as MainItem) }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    internal class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootView: LinearLayout = itemView.findViewById(R.id.root_view)
        val mainIv: ImageView = itemView.findViewById(R.id.mainIv)
        val mainTv: TextView = itemView.findViewById(R.id.mainTv)

    }

}