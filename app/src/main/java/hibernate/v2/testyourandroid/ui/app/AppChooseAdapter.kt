package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.testyourandroid.databinding.ListItemInfoBinding
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
        return ItemViewHolder(
            ListItemInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val itemBinding = (holder as ItemViewHolder).viewBinding

        itemBinding.titleTv.text = item.titleText
        itemBinding.contentTv.text = item.contentText
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { v -> itemClickListener.onItemDetailClick(v.tag as AppChooseItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(val viewBinding: ListItemInfoBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}