package hibernate.v2.testyourandroid.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import hibernate.v2.testyourandroid.databinding.ItemGridSimpleBinding
import hibernate.v2.testyourandroid.model.GridItem

/**
 * Created by himphen on 24/5/16.
 */
class GridItemAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<GridItem, GridItemAdapter.ItemViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<GridItem>() {
        override fun areItemsTheSame(oldItem: GridItem, newItem: GridItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GridItem, newItem: GridItem): Boolean {
            return oldItem.text == newItem.text
        }
    }

    interface ItemClickListener {
        fun onItemDetailClick(gridItem: GridItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(
            ItemGridSimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        val itemBinding = holder.viewBinding
        itemBinding.mainIv.load(item.image)
        itemBinding.mainTv.text = item.text
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as GridItem) }
    }

    class ItemViewHolder(val viewBinding: ItemGridSimpleBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}
