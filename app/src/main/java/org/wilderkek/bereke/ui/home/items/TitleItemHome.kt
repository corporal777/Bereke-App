package org.wilderkek.bereke.ui.home.items

import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemTitleHomeBinding

class TitleItemHome(
    private val title: String,
    private val itemId: Long = -1001L
) : BindableItem<ItemTitleHomeBinding>(itemId) {

    override fun bind(viewBinding: ItemTitleHomeBinding, position: Int) {
        viewBinding.apply {
            if (!title.isNullOrEmpty()) {
                tvTitle.text = title
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is TitleItemHome) return false
        if (title != other.title) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_title_home
}