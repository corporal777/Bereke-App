package org.wilderkek.bereke.di.repo.note

import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import org.wilderkek.bereke.api.ApiService
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.model.body.ActivateNoteBody
import org.wilderkek.bereke.model.body.DeactivateNoteBody
import org.wilderkek.bereke.model.body.DeleteNoteBody
import org.wilderkek.bereke.model.body.NoteComplaintBody
import org.wilderkek.bereke.model.request.*
import org.wilderkek.bereke.model.response.*

class NotesRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    NotesRepository {

    override fun getNotesList(map: Map<String, String>): Maybe<List<NoteModel>> {
        return apiService.getNotesList(map).doOnSuccess {
            it.forEach { note -> setNoteFields(note) }
        }
    }

    override fun getUserFavoriteNotes(map: Map<String, String>): Maybe<List<NoteModel>> {
        return apiService.getUserFavoriteNotes(appData.getUserId(), map).doOnSuccess {
            it.forEach { note -> setNoteFields(note) }
        }
    }

    override fun createNote(body: NoteRequestBody): Maybe<NoteModel> {
        return apiService.createNote(appData.getUserId(), body).doOnSuccess { setNoteFields(it) }
    }

    override fun updateNote(noteId: String, body: NoteRequestBody): Maybe<NoteModel> {
        return apiService.updateNote(noteId, body).doOnSuccess { setNoteFields(it) }
    }

    override fun uploadNoteImages(noteId: String, body: RequestBody): Maybe<NoteModel> {
        return apiService.changeNoteImages(noteId, body).doOnSuccess { setNoteFields(it) }
    }

    override fun createNoteDraft(draft: NoteDraftModel): Maybe<NoteDraftResponseModel> {
        return apiService.createNoteDraft(appData.getUserId(), draft)
    }

    override fun loadNoteDraft(): Maybe<NoteDraftResponseModel> {
        return apiService.getNoteDraft(appData.getUserId())
    }

    override fun addNoteToFavorite(noteId: String): Maybe<NoteModel> {
        return if (appData.isUserAuthorized()) {
            apiService.addNoteToFavorite(NoteLikeBody(noteId, appData.getUserId()))
                .doOnSuccess { setNoteFields(it) }
                .doOnSuccess { appData.sendMessageSubject("Добавлено в избранное") }
        } else Maybe.error(NullPointerException("User is not authorized!"))
    }

    override fun removeNoteFromFavorite(noteId: String): Maybe<NoteModel> {
        return if (appData.isUserAuthorized()) {
            apiService.removeNoteFromFavorite(NoteLikeBody(noteId, appData.getUserId()))
                .doOnSuccess { setNoteFields(it) }
                .doOnSuccess { appData.sendMessageSubject("Удалено из избранного") }
        } else Maybe.error(NullPointerException("User is not authorized!"))

    }

    override fun removeAllUserFavoriteNotes(map: Map<String, String>): Completable {
        return apiService.removeUserFavoriteNotes(map)
    }

    override fun getNoteDetail(id: String): Maybe<NoteDetailModel> {
        return apiService.getNoteDetail(id).doOnSuccess { setNoteFields(it.note) }
    }

    override fun activateNote(noteId: String): Completable {
        return apiService.activateUserNote(ActivateNoteBody(appData.getUserId(), noteId))
    }

    override fun deActivateNote(noteId: String): Completable {
        return apiService.deActivateUserNote(DeactivateNoteBody(appData.getUserId(), noteId))
    }

    override fun deleteNote(noteId: String): Completable {
        return apiService.deleteUserNote(DeleteNoteBody(appData.getUserId(), noteId))
    }

    override fun sendNoteComplaint(body: NoteComplaintBody): Completable {
        return apiService.sendNoteComplaint(body)
    }

    private fun setNoteFields(note: NoteModel) {
        note.setNoteLiked(appData.getUserId())
        note.setOwner(appData.getUserId())
    }
}