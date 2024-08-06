package org.wilderkek.bereke.ui.detailNote.data

import org.wilderkek.bereke.model.request.HouseNoteAdditionalData
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.request.WorkNoteAdditionalData
import org.wilderkek.bereke.model.response.UserModel

sealed class NoteDetailData<T>(
    val note: NoteModel,
    val author: UserModel?,
    var noteData: T?
) {


    abstract fun getData(): T

    class WorkData(field: NoteModel, creator: UserModel?, value: WorkNoteAdditionalData?) : NoteDetailData<WorkNoteAdditionalData>(field, creator, value) {
        override fun getData(): WorkNoteAdditionalData = noteData!!
    }

    class HouseData(field: NoteModel, creator: UserModel?, value: HouseNoteAdditionalData?) : NoteDetailData<HouseNoteAdditionalData>(field, creator, value) {
        override fun getData(): HouseNoteAdditionalData = noteData!!
    }



}