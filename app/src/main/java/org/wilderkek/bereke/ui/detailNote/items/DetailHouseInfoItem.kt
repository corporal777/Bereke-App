package org.wilderkek.bereke.ui.detailNote.items

import androidx.core.view.isVisible
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailHouseInfoBinding
import org.wilderkek.bereke.model.request.HouseNoteAdditionalData
import org.wilderkek.bereke.model.response.NoteModel

class DetailHouseInfoItem (
    private val note: NoteModel,
    private val additionalData: HouseNoteAdditionalData
) : BindableItem<ItemNoteDetailHouseInfoBinding>() {

    private var category: String = note.getCategory()
    private var subCategory: String = additionalData.subCategory
    private var houseType: String? = additionalData.houseType
    private var houseTerm: String? = additionalData.houseTerm
    private var houseRoomCount: String? = additionalData.houseRoomCount
    private var houseComment: String? = additionalData.houseComment


    override fun bind(viewBinding: ItemNoteDetailHouseInfoBinding, position: Int) {
        viewBinding.apply {
            tvCategory.text = category
            tvSubCategory.text = subCategory

            tvHouseType.apply {
                if (houseType.isNullOrEmpty()) {
                    text = root.context.getString(R.string.not_chosen)
                } else text = houseType
            }

            tvHouseRoomsTitle.isVisible = !houseRoomCount.isNullOrEmpty()
            tvHouseRooms.apply {
                isVisible = !houseRoomCount.isNullOrEmpty()
                text = houseRoomCount
            }

            tvHouseTermTitle.isVisible = !houseTerm.isNullOrEmpty()
            tvHouseTerm.apply {
                isVisible = !houseTerm.isNullOrEmpty()
                text = houseTerm
            }

            tvHouseCommentTitle.isVisible = !houseComment.isNullOrEmpty()
            tvHouseComment.apply {
                isVisible = !houseComment.isNullOrEmpty()
                text = houseComment
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailHouseInfoItem) return false
        if (note != other.note) return false
        if (additionalData != other.additionalData) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_house_info
}