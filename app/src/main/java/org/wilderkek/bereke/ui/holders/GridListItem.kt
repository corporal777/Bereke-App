package org.wilderkek.bereke.ui.holders

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemGridListBinding

open class GridListItem<VH : RecyclerView.ViewHolder>(id : Long) : BindableItem<ItemGridListBinding>(id) {

    var adapter: RecyclerView.Adapter<VH>? = null

    override fun bind(viewBinding: ItemGridListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@GridListItem.adapter
        }
    }

    fun getAdapter() = (adapter as GroupAdapter<*>)

    override fun getLayout() = R.layout.item_grid_list
}