package org.wilderkek.bereke.ui.holders

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemHorizontalListBinding

open class HorizontalListItem<VH : RecyclerView.ViewHolder>(id : Long) : BindableItem<ItemHorizontalListBinding>(id) {

    var adapter: RecyclerView.Adapter<VH>? = null

    override fun bind(viewBinding: ItemHorizontalListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@HorizontalListItem.adapter
        }
    }

    fun getAdapter() = (adapter as GroupAdapter<*>)

    override fun getLayout() = R.layout.item_horizontal_list
}