package org.wilderkek.bereke.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.di.repo.story.StoryRepository
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.response.StoriesModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.*

class HomeViewModel(
    appData: AppData,
    private val notesRepository: NotesRepository,
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : BaseViewModel(appData) {

    private val _notesList = MutableLiveData<List<NoteModel?>>(List(6) { null })
    val notesList = _notesList.toSingleEvent()

    private val _storiesList = MutableLiveData<List<StoriesModel>>()
    val storiesList: LiveData<List<StoriesModel>> get() = _storiesList

    private val _updatedNote = MutableLiveData<NoteModel>()
    val updatedNote = _updatedNote.toSingleEvent()

    init {
        //getNotesList()
        //getStoriesList()
    }

    fun getNotesList() {
        compositeDisposable += notesRepository.getNotesList(buildFilters())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _notesList.postValue(emptyList())
                    it.printStackTrace()
                },
                onSuccess = {
                    _notesList.postValue(it)
                })
    }

     fun getStoriesList() {
        compositeDisposable += storyRepository.getStoriesList()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _storiesList.postValue(it)
            }
    }


    fun addNoteToFavorite(noteId: String) {
        compositeDisposable += notesRepository.addNoteToFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _updatedNote.postValue(it)
            }
    }

    fun removeNoteFromFavorite(noteId: String) {
        compositeDisposable += notesRepository.removeNoteFromFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _updatedNote.postValue(it)
            }
    }

    private fun buildFilters(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put(FILTER_STATUS, APPROVED)
            put(FILTER_LIMIT, "10")
        }
    }

}