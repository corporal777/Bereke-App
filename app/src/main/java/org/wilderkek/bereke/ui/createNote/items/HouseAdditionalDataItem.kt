package org.wilderkek.bereke.ui.createNote.items

import android.widget.TextView
import androidx.core.view.isVisible
import com.google.gson.Gson
import org.wilderkek.bereke.util.getHouseSubCategoriesList
import org.wilderkek.bereke.util.getHouseTypesList
import org.wilderkek.bereke.util.setAdapterData
import org.wilderkek.bereke.util.initInput
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemHouseAdditionalDataBinding
import org.wilderkek.bereke.model.request.HouseNoteDataDraft

class HouseAdditionalDataItem(
    val data: String? = null,
    val salary: String? = null
) : AdditionalDataItem<ItemHouseAdditionalDataBinding>() {

    private var subCategory = ""
    private var houseType = ""
    private var houseRooms = ""
    private var houseTerm = ""
    private var housePrice = ""
    private var houseComment = ""

    private var roomsVisible = houseType == "Квартира" || houseType == "Гостиница"

    init {
        if (!data.isNullOrEmpty()) {
            val draft = Gson().fromJson(data, HouseNoteDataDraft::class.java)
            subCategory = draft.subCategory
            houseType = draft.houseType ?: ""
            houseRooms = draft.houseRooms ?: ""
            houseTerm = draft.houseTerm ?: ""
            housePrice = salary ?: ""
            houseComment = draft.houseComment ?: ""
            roomsVisible = houseType == "Квартира" || houseType == "Гостиница"
        }
    }

    private lateinit var mBinding: ItemHouseAdditionalDataBinding
    override fun bind(viewBinding: ItemHouseAdditionalDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tvNoteSubCategoryRequired.isVisible = subCategory.isNullOrEmpty()
            etSubCategory.apply {
                setAdapterData(getHouseSubCategoriesList())
                initInput(subCategory) {
                    tvNoteSubCategoryRequired.apply {
                        if (!it.toString().isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.toString().isNullOrBlank()
                    }
                    subCategory = it.toString()
                }
            }
            etType.apply {
                setAdapterData(getHouseTypesList())
                initInput(houseType) {
                    houseType = it.toString()
                }
                setOnItemClickListener { _, _, _, _ ->
                    roomsVisible = houseType == "Квартира" || houseType == "Гостиница"
                    tvHouseRoomsTitle.isVisible = roomsVisible
                    tilRooms.isVisible = roomsVisible
                    houseRooms = ""
                    etRooms.text?.clear()
                }
            }
            tilRooms.apply {
                tvHouseRoomsTitle.isVisible = roomsVisible
                isVisible = roomsVisible
            }
            etRooms.initInput(houseRooms) {
                houseRooms = it.toString()
            }
            etTerm.initInput(houseTerm) {
                houseTerm = it.toString()
            }
            etPrice.initInput(housePrice) {
                housePrice = it.toString()
            }
            etComment.initInput(houseComment) {
                houseComment = it.toString()
            }
        }
    }


    override fun getDataToSave(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put("subCategory", subCategory)
            put("houseType", houseType)
            put("houseRooms", houseRooms)
            put("houseTerm", houseTerm)
            put("houseComment", houseComment)
            put("salary", housePrice)
        }
    }

    override fun getDraftToSave(): String {
        val draft = HouseNoteDataDraft(
            subCategory,
            houseType,
            houseRooms,
            houseTerm,
            houseComment
        )
        return Gson().toJson(draft)
    }

    override fun isAdditionalDataValid(): Boolean {
        var valid = true
        if (subCategory.isNullOrBlank()) {
            valid = false
            mBinding.tvNoteSubCategoryRequired.changeRequiredTextColor(false)
        }
        return valid
    }

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }


    override fun getLayout() = R.layout.item_house_additional_data


}