package org.wilderkek.bereke.ui.holders

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import org.wilderkek.bereke.R

class PlaceholderItem(
    private val type: Type
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout() = when (type) {
        Type.NOTE -> R.layout.item_note_placeholder
    }

    enum class Type {
        NOTE
    }
}