package hibernate.v2.testyourandroid.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import hibernate.v2.testyourandroid.databinding.GridItemTestBinding
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
            GridItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val itemBinding = (holder as ItemViewHolder).viewBinding
        Glide.with(itemBinding.mainIv.context)
            .load(item.image)
            .apply(
                RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(itemBinding.mainIv)

        itemBinding.mainTv.text = item.text
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as GridItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(val viewBinding: GridItemTestBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}