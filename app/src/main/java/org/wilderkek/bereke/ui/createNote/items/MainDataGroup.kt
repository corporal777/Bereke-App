package org.wilderkek.bereke.ui.createNote.items

import android.content.Context
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import org.wilderkek.bereke.util.findItemBy
import org.wilderkek.bereke.util.updateItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.model.response.NoteAddressModel
import org.wilderkek.bereke.model.request.NoteDraftModel
import org.wilderkek.bereke.model.response.NoteRequestBody

class MainDataGroup(
    private val context: Context,
    private val noteDraft: NoteDraftModel?
) : NestedGroup() {

    private val mainDataItem = MainDataItem(
        noteDraft?.name,
        noteDraft?.description,
        noteDraft?.category,
        noteDraft?.contacts?.phone,
        noteDraft?.contacts?.whatsapp
    ).apply {
        onCategoryChangeCallback = {
            when (it) {
                "work" -> additionalDataSection.updateItem(WorkAdditionalDataItem())
                "house" -> additionalDataSection.updateItem(HouseAdditionalDataItem())
            }
        }

    }
    private val additionalDataSection = Section().apply {
        setHeader(TitleDataItem(context.getString(R.string.additional_information)))
        setHideWhenEmpty(true)
    }


    init {
        if (noteDraft != null) {
            when (noteDraft.category) {
                "work" -> additionalDataSection.updateItem(
                    WorkAdditionalDataItem(
                        noteDraft.additionalData,
                        noteDraft.salary
                    )
                )
                "house" -> additionalDataSection.updateItem(
                    HouseAdditionalDataItem(
                        noteDraft.additionalData,
                        noteDraft.salary
                    )
                )
            }
        }
        mainDataItem.registerGroupDataObserver(this)
        additionalDataSection.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mainDataItem
            1 -> additionalDataSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mainDataItem -> 0
            additionalDataSection -> 1
            else -> -1
        }
    }

    private fun isMainDataValid() = mainDataItem.isMainDataValid()
    fun isFieldsEmpty() = mainDataItem.isFieldsIsEmpty()

    private fun isAdditionalDataValid(): Boolean {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        return item?.isAdditionalDataValid() ?: false
    }


    private fun getAdditionalData(): MutableMap<String, String> {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        return item?.getDataToSave() ?: mutableMapOf<String, String>()
    }

    fun getDraftData(addressDataItem: AddressDataItem?): NoteDraftModel {
        val mainData = mainDataItem.getMainData()
        val contactsData = mainDataItem.getContactsData()
        val salary = getAdditionalData()["salary"]
        val address = addressDataItem?.getAddressData() ?: NoteAddressModel(
            "",
            "",
            null,
            null,
            emptyList(),
            null,
            null
        )
        val additionalData =
            additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }?.getDraftToSave()

        return NoteDraftModel(
            mainData["name"] ?: "",
            mainData["description"] ?: "",
            salary ?: "",
            mainData["category"] ?: "",
            contactsData,
            address,
            additionalData ?: ""
        )
    }

    fun getNoteRequest(addressDataItem: AddressDataItem?): NoteRequestBody? {
        val mainData = mainDataItem.getMainData()
        val contactsData = mainDataItem.getContactsData()
        val additionalData = getAdditionalData()
        var noteRequestBody: NoteRequestBody? = null

        if (isMainDataValid()) {
            if (addressDataItem != null && addressDataItem.isAddressDataValid()) {
                if (isAdditionalDataValid()) {
                    noteRequestBody = NoteRequestBody(
                        mainData["name"] ?: "",
                        mainData["description"] ?: "",
                        additionalData["salary"] ?: "",
                        mainData["category"] ?: "",
                        null,
                        contactsData,
                        addressDataItem.getAddressData(),
                        additionalData
                    )
                }
            }
        }
        return noteRequestBody
    }

    override fun getGroupCount() = 2
}