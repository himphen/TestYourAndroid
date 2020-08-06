package hibernate.v2.testyourandroid.ui.base

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
import hibernate.v2.testyourandroid.model.GridItem

/**
 * Created by himphen on 24/5/16.
 */
class GridItemAdapter(
    private val list: List<GridItem>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(gridItem: GridItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item_test, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        holder as ItemViewHolder
        Glide.with(holder.mainIv.context)
            .load(item.image)
            .apply(
                RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(holder.mainIv)

        holder.mainTv.text = item.text
        holder.rootView.tag = item
        holder.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as GridItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rootView: LinearLayout = view.findViewById(R.id.root_view)
        var mainIv: ImageView = view.findViewById(R.id.mainIv)
        var mainTv: TextView = view.findViewById(R.id.mainTv)
    }

}