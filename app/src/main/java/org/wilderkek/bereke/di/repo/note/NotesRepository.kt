package org.wilderkek.bereke.di.repo.note

import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import org.wilderkek.bereke.model.body.NoteComplaintBody
import org.wilderkek.bereke.model.request.NoteDraftModel
import org.wilderkek.bereke.model.request.NoteDraftResponseModel
import org.wilderkek.bereke.model.response.NoteDetailModel
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.response.NoteRequestBody

interface NotesRepository {
    fun getNotesList(map: Map<String, String>): Maybe<List<NoteModel>>
    fun getUserFavoriteNotes(map: Map<String, String>): Maybe<List<NoteModel>>
    fun createNote(body: NoteRequestBody): Maybe<NoteModel>
    fun updateNote(noteId : String, body: NoteRequestBody): Maybe<NoteModel>
    fun uploadNoteImages(noteId: String, body: RequestBody) : Maybe<NoteModel>

    fun createNoteDraft(draft : NoteDraftModel) : Maybe<NoteDraftResponseModel>
    fun loadNoteDraft() : Maybe<NoteDraftResponseModel>

    fun addNoteToFavorite(noteId: String) : Maybe<NoteModel>
    fun removeNoteFromFavorite(noteId: String) : Maybe<NoteModel>

    fun removeAllUserFavoriteNotes(map : Map<String, String>) : Completable

    fun getNoteDetail(id : String): Maybe<NoteDetailModel>

    fun activateNote(noteId: String): Completable
    fun deActivateNote(noteId: String): Completable
    fun deleteNote(noteId: String): Completable

    fun sendNoteComplaint(body : NoteComplaintBody): Completable
}