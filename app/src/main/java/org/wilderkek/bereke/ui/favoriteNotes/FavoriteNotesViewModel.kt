package org.wilderkek.bereke.ui.favoriteNotes

import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.*

class FavoriteNotesViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel(appData) {

    private val _hasFavoriteNotes = MutableLiveData<Boolean>(false)
    val hasFavoriteNotes = _hasFavoriteNotes.toSingleEvent()

    private val filters = arrayListOf<FavoriteFilter>(
        FavoriteFilter(1, "Вакансия", false),
        FavoriteFilter(2, "Подработка", false),
        FavoriteFilter(3, "Квартира", false),
        FavoriteFilter(4, "Комната", false),
        FavoriteFilter(5, "Авто", false),
        FavoriteFilter(6, "Услуги", false),
    )

    private var filterLoading = false

    private val _notesList = MutableLiveData<List<NoteModel?>>(List(6) { null })
    val notesList = _notesList.toSingleEvent()

    private val _placeholder = MutableLiveData<String>()
    val placeholder = _placeholder.toSingleEvent()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        if (isUserAuthorized()) {
            compositeDisposable += notesRepository.getUserFavoriteNotes(buildFilters())
                .doOnSuccess { _hasFavoriteNotes.postValue(!it.isNullOrEmpty()) }
                .performOnBackgroundOutOnMain()
                .let {
                    if (filterLoading) {
                        filterLoading = false
                        it.withLoadingDialog(progressData)
                    } else it
                }
                .subscribeSimple(
                    onError = { _placeholder.postValue("Нет результатов") },
                    onSuccess = {
                        if (it.isEmpty()) {
                            if (filters.any { x -> x.isChecked }) _placeholder.postValue("Нет результатов")
                            else _placeholder.postValue("В избранном пусто")
                        } else _notesList.postValue(it)
                    })
        } else _placeholder.postValue("Нет результатов")
    }

    fun onFilterChanged(filter: FavoriteFilter) {
        filters.find { x -> x.id == filter.id }?.isChecked = filter.isChecked
        filterLoading = true
        loadNotes()
    }

    fun addNoteToFavorite(noteId: String) {
        compositeDisposable += notesRepository.addNoteToFavorite(noteId)
            .flatMap { notesRepository.getUserFavoriteNotes(buildFilters()) }
            .doOnSuccess { _hasFavoriteNotes.postValue(!it.isNullOrEmpty()) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _notesList.postValue(it)
            }
    }

    fun removeNoteFromFavorite(noteId: String) {
        compositeDisposable += notesRepository.removeNoteFromFavorite(noteId)
            .flatMap { notesRepository.getUserFavoriteNotes(buildFilters()) }
            .doOnSuccess { _hasFavoriteNotes.postValue(!it.isNullOrEmpty()) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _notesList.postValue(it)
                if (it.isEmpty()) _placeholder.postValue("В избранном пусто")
            }
    }

    fun removeAllFavoriteNotes(status: String) {
        compositeDisposable += notesRepository.removeAllUserFavoriteNotes(
            mapOf(
                FILTER_USER to appData.getUserId(),
                FILTER_STATUS to status
            )
        )
            .andThen(notesRepository.getUserFavoriteNotes(emptyMap()))
            .doOnSuccess { _hasFavoriteNotes.postValue(!it.isNullOrEmpty()) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                if (it.isEmpty()) _placeholder.postValue("В избранном пусто")
                else _notesList.postValue(it)
            }
    }

    private fun buildFilters(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            if (filters.any { x -> x.isChecked }) {
                val listSub = arrayListOf<String>()
                val listHouse = arrayListOf<String>()
                filters.filter { x -> x.isChecked }.forEach {
                    if (it.id == 1 || it.id == 2) listSub.add(it.name)
                    if (it.id == 3 || it.id == 4) listHouse.add(it.name)
                }
                if (!listSub.isNullOrEmpty()) put(FILTER_SUBCATEGORY, listSub.joinToString(",") { it })
                if (!listHouse.isNullOrEmpty()) put(FILTER_HOUSE_TYPE, listHouse.joinToString(",") { it })
            }
        }
    }

    fun getFavoriteFilters() = filters
}

class FavoriteFilter(
    val id: Int,
    val name: String,
    var isChecked: Boolean
)