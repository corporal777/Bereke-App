package org.wilderkek.bereke.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.*

class MyNotesViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel(appData) {

    private var currentPosition = 0
    private var isFirstLaunch = true

    private val _tabPosition = MutableLiveData<Int>()
    val tabPosition: LiveData<Int> get() = _tabPosition

    private val _activeNotes = MutableLiveData<List<NoteModel>?>()
    val activeNotes get() = _activeNotes.toSingleEvent()

    private val _inActiveNotes = MutableLiveData<List<NoteModel>?>()
    val inActiveNotes get() = _inActiveNotes.toSingleEvent()

    private val _moderationNotes = MutableLiveData<List<NoteModel>?>()
    val moderationNotes get() = _moderationNotes.toSingleEvent()

    fun setTabPosition(position: Int) {
        currentPosition = position
        _tabPosition.value = position
    }

    init {
        getNotes()
    }

    private fun getNotes() {
        val notes = CustomTriple<List<NoteModel>>()
        compositeDisposable += Maybe.zip(
            loadNotes(APPROVED),
            loadNotes(INACTIVE),
            loadNotes(PENDING)
        )
        { a, i, m ->
            notes.first = a
            notes.second = i
            notes.third = m
        }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .subscribeSimple(
                onError = {
                    _activeNotes.postValue(notes.first)
                    _inActiveNotes.postValue(notes.second)
                    _moderationNotes.postValue(notes.third)
                },
                onSuccess = {
                    _activeNotes.postValue(notes.first)
                    _inActiveNotes.postValue(notes.second)
                    _moderationNotes.postValue(notes.third)
                }
            )
    }

    fun activateUserNote(noteId: String) {
        compositeDisposable += notesRepository.activateNote(noteId)
            .andThen(loadNotes(APPROVED).doOnSuccess { _activeNotes.postValue(it) })
            .ignoreElement()
            .andThen(loadNotes(INACTIVE))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _inActiveNotes.postValue(it)
            }
    }

    fun deActivateUserNote(noteId: String) {
        compositeDisposable += notesRepository.deActivateNote(noteId)
            .andThen(loadNotes(INACTIVE).doOnSuccess { _inActiveNotes.postValue(it) })
            .ignoreElement()
            .andThen(loadNotes(APPROVED))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _activeNotes.postValue(it)
            }
    }

    fun deleteUserNote(noteId: String) {
        compositeDisposable += notesRepository.deleteNote(noteId)
            .andThen(Maybe.zip(loadNotes(APPROVED), loadNotes(INACTIVE), loadNotes(PENDING))
            { a, i, m -> CustomTriple(a, i, m) })
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _activeNotes.postValue(it.first)
                _inActiveNotes.postValue(it.second)
                _moderationNotes.postValue(it.third)
            }

    }

    private fun loadNotes(status: String): Maybe<List<NoteModel>> {
        return notesRepository.getNotesList(
            mapOf(
                FILTER_USER to appData.getUserId(),
                FILTER_STATUS to status
            )
        )
    }
}