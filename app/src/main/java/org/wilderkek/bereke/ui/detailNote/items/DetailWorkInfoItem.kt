package org.wilderkek.bereke.ui.detailNote.items

import androidx.core.view.isVisible
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailWorkInfoBinding
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.request.WorkNoteAdditionalData

class DetailWorkInfoItem(
    private val note: NoteModel,
    private val additionalData: WorkNoteAdditionalData
) : BindableItem<ItemNoteDetailWorkInfoBinding>() {

    private var category: String = note.getCategory()
    private var subCategory: String = additionalData.subCategory
    private var speciality: String? = additionalData.workSpeciality
    private var workSchedule: String? = additionalData.workSchedule
    private var workExperience: String? = additionalData.workExperience
    private var workComment: String? = additionalData.workComment


    override fun bind(viewBinding: ItemNoteDetailWorkInfoBinding, position: Int) {
        viewBinding.apply {
            tvCategory.text = category
            tvSubCategory.text = subCategory

            tvWorkSpeciality.apply {
                if (speciality.isNullOrEmpty()) {
                    text = root.context.getString(R.string.not_chosen)
                } else text = speciality
            }

            tvWorkSchedule.apply {
                if (workSchedule.isNullOrEmpty()) {
                    text = root.context.getString(R.string.not_chosen)
                } else text = workSchedule
            }
            tvWorkExperience.apply {
                if (workExperience.isNullOrEmpty())
                    text = root.context.getString(R.string.not_chosen)
                else text = workExperience
            }

            lnWorkComment.isVisible = !workComment.isNullOrEmpty()
            tvWorkComment.text = workComment
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailWorkInfoItem) return false
        if (note != other.note) return false
        if (additionalData != other.additionalData) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_work_info
}