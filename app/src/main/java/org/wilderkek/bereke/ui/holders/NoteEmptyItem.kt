package org.wilderkek.bereke.ui.holders

import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteEmptyBinding

class NoteEmptyItem(
    private val title: String
) : BindableItem<ItemNoteEmptyBinding>() {


    override fun bind(viewBinding: ItemNoteEmptyBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is NoteEmptyItem) return false
        if (title != other.title) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_empty
}