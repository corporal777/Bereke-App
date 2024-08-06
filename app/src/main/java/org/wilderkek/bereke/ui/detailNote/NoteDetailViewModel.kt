package org.wilderkek.bereke.ui.detailNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.model.body.NoteComplaintBody
import org.wilderkek.bereke.model.request.HouseNoteAdditionalData
import org.wilderkek.bereke.model.response.NoteDetailModel
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.request.WorkNoteAdditionalData
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.ui.detailNote.data.NoteDetailData
import org.wilderkek.bereke.util.*

class NoteDetailViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel(appData) {

    var noteId = ""
    private var scrollOffset = 0
    private val _scrollOffsetData = MutableLiveData<Int>(scrollOffset)
    val scrollOffsetData get() = _scrollOffsetData.toSingleEvent()

    private val _noteDetail = MutableLiveData<NoteDetailData<*>>()
    val noteDetail: LiveData<NoteDetailData<*>> get() = _noteDetail

    private val _sameNotesList = MutableLiveData<List<NoteModel>>()
    val sameNotesList: LiveData<List<NoteModel>> get() = _sameNotesList

    private val _updatedSameNote = MutableLiveData<NoteModel>()
    val updatedSameNote = _updatedSameNote.toSingleEvent()

    private val _noteLiked = MutableLiveData<Boolean>()
    val noteLiked get() = _noteLiked.toSingleEvent()


    fun loadNoteDetail(id: String) {
        compositeDisposable += notesRepository.getNoteDetail(id)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .map { createNoteDetailData(it) }
            .subscribeSimple {
                if (it != null) {
                    _noteDetail.postValue(it)
                    _noteLiked.postValue(it.note.isNoteLiked)
                    loadSameNotes(it)
                }
            }
    }

    private fun loadSameNotes(note: NoteDetailData<*>) {
        if (note.noteData != null) {
            compositeDisposable += notesRepository.getNotesList(buildFilters(note))
                .map { it.filter { x -> x.id != noteId } }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    if (!it.isNullOrEmpty()) _sameNotesList.postValue(it)
                }
        }
    }

    fun addNoteToFavorite(noteId: String) {
        compositeDisposable += notesRepository.addNoteToFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                if (this.noteId != it.id) _updatedSameNote.postValue(it)
                else _noteLiked.postValue(it.isNoteLiked)
            }
    }

    fun removeNoteFromFavorite(noteId: String) {
        compositeDisposable += notesRepository.removeNoteFromFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                if (this.noteId != it.id) _updatedSameNote.postValue(it)
                else _noteLiked.postValue(it.isNoteLiked)
            }
    }

    fun sendNoteComplaint(message: String) {
        compositeDisposable += notesRepository.sendNoteComplaint(
            NoteComplaintBody(generateId(), noteId, message)
        )
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                appData.sendMessageSubject("Жалоба отправлена")
            }
    }

    private fun createNoteDetailData(noteDetail: NoteDetailModel): NoteDetailData<*>? {
        return when (noteDetail.note.category) {
            NoteModel.Category.WORK -> {
                NoteDetailData.WorkData(
                    noteDetail.note,
                    noteDetail.creator,
                    noteDetail.additionalData.fromJson<WorkNoteAdditionalData>()
                )
            }
            NoteModel.Category.HOUSE -> {
                NoteDetailData.HouseData(
                    noteDetail.note,
                    noteDetail.creator,
                    noteDetail.additionalData.fromJson<HouseNoteAdditionalData>()
                )
            }
            else -> null
        }
    }

    fun changeScrollOffset(value: Int) {
        scrollOffset = value
        _scrollOffsetData.postValue(scrollOffset)
    }


    private fun buildFilters(data: NoteDetailData<*>): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put(FILTER_STATUS, APPROVED)
            put(FILTER_CATEGORY, data.note.getCategoryValue())
            when (data) {
                is NoteDetailData.WorkData -> {
                    put(FILTER_SUBCATEGORY, data.noteData?.subCategory ?: "")
                    put(FILTER_OBJECT_TYPE, data.noteData?.workSpeciality ?: "")
                }
                is NoteDetailData.HouseData -> {
                    put(FILTER_SUBCATEGORY, data.noteData?.subCategory ?: "")
                    put(FILTER_OBJECT_TYPE, data.noteData?.houseType ?: "")
                }
            }
        }
    }
}