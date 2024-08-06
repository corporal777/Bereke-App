package org.wilderkek.bereke.ui.detailNote.items

import android.view.Gravity
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailMainInfoBinding

class DetailMainInfoItem(
    private val name: String,
    private val salary: String,
    private val description: String?,
    private val likes: List<String>?,
    private val viewings: String
) : BindableItem<ItemNoteDetailMainInfoBinding>() {


    override fun bind(viewBinding: ItemNoteDetailMainInfoBinding, position: Int) {
        viewBinding.apply {
            tvName.text = name
            tvDescription.apply {
                if (!description.isNullOrEmpty()) text = description
                else {
                    gravity = Gravity.CENTER
                    text = root.context.getString(R.string.no_description)
                }
            }
            tvSalary.text = salary


            tvLikes.text = if (likes.isNullOrEmpty()) "0"
            else likes.size.toString()

            tvViewings.text = viewings
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailMainInfoItem) return false
        if (name != other.name) return false
        if (salary != other.salary) return false
        if (description != other.description) return false
        if (likes != other.likes) return false
        if (viewings != other.viewings) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_main_info
}