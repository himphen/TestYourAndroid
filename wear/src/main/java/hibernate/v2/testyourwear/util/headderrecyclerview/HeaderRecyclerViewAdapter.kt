package hibernate.v2.testyourwear.util.headderrecyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * RecyclerView.Adapter extension created to add header capability support and a generic List of
 * items really useful most of the cases. You should extend from this class and override
 * onCreateViewHolder to create your ViewHolder instances and onBindViewHolder methods to draw your
 * user interface as you wish.
 *
 * The usage of List<T> items member is not mandatory. If you are going to provide your custom
 * implementation remember to override getItemCount method.
</T> */
abstract class HeaderRecyclerViewAdapter<VH : RecyclerView.ViewHolder, H, T, F> :
    RecyclerView.Adapter<VH>() {
    /**
     * Get header data in this adapter, you should previously use [.setHeader]
     * in the adapter initialization code to set header data.
     *
     * @return header data
     */
    var header: H? = null
    private var items: List<T> = listOf()

    /**
     * Get footer data in this adapter, you should previously use [.setFooter]
     * in the adapter initialization code to set footer data.
     *
     * @return footer data
     */
    private var footer: F? = null
    private var showFooter = false

    /**
     * Invokes onCreateHeaderViewHolder, onCreateItemViewHolder or onCreateFooterViewHolder methods
     * based on the view type param.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
         when {
            isHeaderType(viewType) -> onCreateHeaderViewHolder(parent, viewType)?.let {
                return it
            }
            isFooterType(viewType) -> onCreateFooterViewHolder(parent, viewType)?.let {
                return it
            }
        }

        return onCreateItemViewHolder(parent, viewType)
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected open fun onCreateHeaderViewHolder(parent: ViewGroup, viewType: Int): VH? {
        return null
    }

    protected abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * If you don't need footer feature, you can bypass overriding this method.
     */
    protected open fun onCreateFooterViewHolder(parent: ViewGroup, viewType: Int): VH? {
        return null
    }

    /**
     * Invokes onBindHeaderViewHolder, onBindItemViewHolder or onBindFooterViewHOlder methods based
     * on the position param.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        when {
            isHeaderPosition(position) -> onBindHeaderViewHolder(holder, position)
            isFooterPosition(position) -> onBindFooterViewHolder(holder, position)
            else -> onBindItemViewHolder(holder, position)
        }
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected open fun onBindHeaderViewHolder(holder: VH, position: Int) {}
    protected abstract fun onBindItemViewHolder(holder: VH, position: Int)

    /**
     * If you don't need footer feature, you can bypass overriding this method.
     */
    protected open fun onBindFooterViewHolder(holder: VH, position: Int) {}

    /**
     * Invokes onHeaderViewRecycled, onItemViewRecycled or onFooterViewRecycled methods based
     * on the holder.getAdapterPosition()
     */
    override fun onViewRecycled(holder: VH) {
        val position = holder.adapterPosition
        when {
            isHeaderPosition(position) -> onHeaderViewRecycled(holder)
            isFooterPosition(position) -> onFooterViewRecycled(holder)
            else -> onItemViewRecycled(holder)
        }
    }

    protected open fun onHeaderViewRecycled(holder: VH) {}
    protected open fun onItemViewRecycled(holder: VH) {}
    protected open fun onFooterViewRecycled(holder: VH) {}

    /**
     * Returns the type associated to an item given a position passed as arguments. If the position
     * is related to a header item returns the constant TYPE_HEADER or TYPE_FOOTER if the position is
     * related to the footer, if not, returns TYPE_ITEM.
     *
     * If your application has to support different types override this method and provide your
     * implementation. Remember that TYPE_HEADER, TYPE_ITEM and TYPE_FOOTER are internal constants
     * can be used to identify an item given a position, try to use different values in your
     * application.
     */
    override fun getItemViewType(position: Int): Int {
        var viewType = TYPE_ITEM
        if (isHeaderPosition(position)) {
            viewType = TYPE_HEADER
        } else if (isFooterPosition(position)) {
            viewType = TYPE_FOOTER
        }
        return viewType
    }

    /**
     * Returns the items list size if there is no a header configured or the size taking into account
     * that if a header or a footer is configured the number of items returned is going to include
     * this elements.
     */
    override fun getItemCount(): Int {
        var size = items.size
        if (hasHeader()) {
            size++
        }
        if (hasFooter()) {
            size++
        }
        return size
    }

    /**
     * Get item data in this adapter with the specified postion,
     * you should previously use [.setHeader]
     * in the adapter initialization code to set header data.
     *
     * @return item data in the specified postion
     */
    fun getItem(position: Int): T {
        var i = position
        if (hasHeader() && hasItems()) {
            --i
        }
        return items[i]
    }

    /**
     * You should set header data in the adapter initialization code.
     *
     * @param items item data list
     */
    fun setItems(items: List<T>) {
        validateItems(items)
        this.items = items
    }

    /**
     * Call this method to show hiding footer.
     */
    fun showFooter() {
        showFooter = true
        notifyDataSetChanged()
    }

    /**
     * Call this method to hide footer.
     */
    fun hideFooter() {
        showFooter = false
        notifyDataSetChanged()
    }

    /**
     * Returns true if the position type parameter passed as argument is equals to 0 and the adapter
     * has a not null header already configured.
     */
    fun isHeaderPosition(position: Int): Boolean {
        return hasHeader() && position == 0
    }

    /**
     * Returns true if the position type parameter passed as argument is equals to
     * `getItemCount() - 1`
     * and the adapter has a not null header already configured.
     */
    fun isFooterPosition(position: Int): Boolean {
        val lastPosition = itemCount - 1
        return hasFooter() && position == lastPosition
    }

    /**
     * Returns true if the view type parameter passed as argument is equals to TYPE_HEADER.
     */
    private fun isHeaderType(viewType: Int): Boolean {
        return viewType == TYPE_HEADER
    }

    /**
     * Returns true if the view type parameter passed as argument is equals to TYPE_FOOTER.
     */
    private fun isFooterType(viewType: Int): Boolean {
        return viewType == TYPE_FOOTER
    }

    /**
     * Returns true if the header configured is not null.
     */
    private fun hasHeader(): Boolean {
        return header != null
    }

    /**
     * Returns true if the footer configured is not null.
     */
    private fun hasFooter(): Boolean {
        return footer != null && showFooter
    }

    /**
     * Returns true if the item configured is not empty.
     */
    private fun hasItems(): Boolean {
        return items.isNotEmpty()
    }

    private fun validateItems(items: List<T>?) {
        requireNotNull(items) { "You can't use a null List<Item> instance." }
    }

    companion object {
        protected const val TYPE_HEADER = -2
        protected const val TYPE_ITEM = -1
        protected const val TYPE_FOOTER = -3
    }
}