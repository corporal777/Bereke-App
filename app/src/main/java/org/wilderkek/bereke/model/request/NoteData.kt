package org.wilderkek.bereke.model.request

import com.google.gson.Gson
import org.wilderkek.bereke.model.response.NoteAddressModel
import org.wilderkek.bereke.model.response.NoteContactsModel


data class WorkNoteAdditionalData(
    val noteId: String,
    val subCategory: String,
    val workSchedule: String?,
    val workExperience: String?,
    val workSpeciality: String?,
    val workComment: String?
) {
    fun toDraft(): String? {
        val draft = WorkNoteDataDraft(
            subCategory,
            workSchedule,
            workExperience,
            workSpeciality,
            workComment
        )
        return Gson().toJson(draft)
    }
}

data class HouseNoteAdditionalData(
    val noteId: String,
    val subCategory: String,
    val houseType: String?,
    val houseTerm: String?,
    val houseRoomCount: String?,
    val houseComment: String?
) {
    fun toDraft(): String? {
        val draft = HouseNoteDataDraft(
            subCategory,
            houseType,
            houseRoomCount,
            houseTerm,
            houseComment
        )
        return Gson().toJson(draft)
    }
}

data class NoteDraftResponseModel(
    val user: String,
    val draft: NoteDraftModel
)

data class NoteDraftModel(
    val name: String,
    val description: String?,
    val salary: String?,
    val category: String,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel,
    val additionalData: String
)

data class WorkNoteDataDraft(
    val subCategory: String,
    val workSchedule: String?,
    val workExperience: String?,
    val workSpeciality: String?,
    val workComment: String?
)

data class HouseNoteDataDraft(
    val subCategory: String,
    val houseType: String?,
    val houseRooms: String?,
    val houseTerm: String?,
    val houseComment: String?
)