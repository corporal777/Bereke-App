package org.wilderkek.bereke.ui.createNote.items

import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemCreateNoteDataTitleBinding

class TitleDataItem(private val title: String) : BindableItem<ItemCreateNoteDataTitleBinding>() {


    override fun bind(viewBinding: ItemCreateNoteDataTitleBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun getLayout(): Int = R.layout.item_create_note_data_title
}