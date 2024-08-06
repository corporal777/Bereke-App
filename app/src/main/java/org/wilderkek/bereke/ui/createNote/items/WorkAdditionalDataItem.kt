package org.wilderkek.bereke.ui.createNote.items

import android.widget.TextView
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.xwray.groupie.Item
import org.wilderkek.bereke.util.getWorkSchedulesList
import org.wilderkek.bereke.util.getWorkSubCategoriesList
import org.wilderkek.bereke.util.setAdapterData
import org.wilderkek.bereke.util.initInput
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemWorkAdditionalDataBinding
import org.wilderkek.bereke.model.request.WorkNoteDataDraft

class WorkAdditionalDataItem(
    val data: String? = null,
    val salary: String? = null
) : AdditionalDataItem<ItemWorkAdditionalDataBinding>() {

    private var subCategory = ""
    private var workSchedule = ""
    private var workExperience = ""
    private var workSpeciality = ""
    private var workSalary = ""
    private var workComment = ""


    init {
        if (!data.isNullOrEmpty()) {
            val draft = Gson().fromJson(data, WorkNoteDataDraft::class.java)
            subCategory = draft.subCategory
            workSchedule = draft.workSchedule ?: ""
            workExperience = draft.workExperience ?: ""
            workSpeciality = draft.workSpeciality ?: ""
            workSalary = salary ?: ""
            workComment = draft.workComment ?: ""
        }
    }

    private lateinit var mBinding: ItemWorkAdditionalDataBinding
    override fun bind(viewBinding: ItemWorkAdditionalDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tvNoteSubCategoryRequired.isVisible = subCategory.isNullOrEmpty()
            etSubCategory.apply {
                setAdapterData(getWorkSubCategoriesList())
                initInput(subCategory) {
                    tvNoteSubCategoryRequired.apply {
                        if (!it.toString().isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.toString().isNullOrBlank()
                    }
                    subCategory = it.toString()
                }
            }
            etSchedule.apply {
                setAdapterData(getWorkSchedulesList())
                initInput(workSchedule) {
                    workSchedule = it.toString()
                }
            }

            etSpeciality.apply {
                setTextWithoutSearch(workSpeciality)
                onDataChangedListener = {
                    workSpeciality = it
                }
            }

            etExperience.initInput(workExperience) {
                workExperience = it.toString()
            }

            etSalary.initInput(workSalary) {
                workSalary = it.toString()
            }
            etComment.initInput(workComment) {
                workComment = it.toString()
            }
        }
    }


    override fun isAdditionalDataValid(): Boolean {
        var valid = true
        if (subCategory.isNullOrBlank()) {
            valid = false
            mBinding.tvNoteSubCategoryRequired.changeRequiredTextColor(false)
        }
        return valid
    }

    override fun getDataToSave(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put("subCategory", subCategory)
            put("workSchedule", workSchedule)
            put("workExperience", workExperience)
            put("workSpeciality", workSpeciality)
            put("workComment", workComment)
            put("salary", workSalary)
        }
    }

    override fun getDraftToSave(): String {
        val draft = WorkNoteDataDraft(
            subCategory,
            workSchedule,
            workExperience,
            workSpeciality,
            workComment
        )

        return Gson().toJson(draft)
    }


    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is WorkAdditionalDataItem) return false
        if (data != other.data) return false
        return true
    }

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }


    override fun getLayout(): Int = R.layout.item_work_additional_data
}