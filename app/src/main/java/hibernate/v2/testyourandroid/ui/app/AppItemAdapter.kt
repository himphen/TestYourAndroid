package hibernate.v2.testyourandroid.ui.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import hibernate.v2.testyourandroid.databinding.ItemListInfoAppBinding
import hibernate.v2.testyourandroid.model.AppItem

/**
 * Created by himphen on 25/5/16.
 */
class AppItemAdapter(
    private val list: List<AppItem>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemDetailClick(appItem: AppItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ItemListInfoAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(rawHolder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val itemBinding = (rawHolder as ItemViewHolder).viewBinding

        itemBinding.titleTv.text = item.appName
        itemBinding.contentTv.text = item.packageName
        itemBinding.iconIv.load(item.icon)
        itemBinding.systemAppIndicator.visibility =
            if (item.isSystemApp) View.VISIBLE else View.GONE
        itemBinding.rootView.tag = item
        itemBinding.rootView.setOnClickListener { view -> itemClickListener.onItemDetailClick(view.tag as AppItem) }
    }

    override fun getItemCount(): Int = list.size

    internal class ItemViewHolder(val viewBinding: ItemListInfoAppBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}