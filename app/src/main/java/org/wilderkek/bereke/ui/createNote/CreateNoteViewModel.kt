package org.wilderkek.bereke.ui.createNote

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.model.request.NoteDraftModel
import org.wilderkek.bereke.model.response.NoteRequestBody
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.imagepicker.model.LocalImage
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.rxtakephoto.RxTakePhoto
import org.wilderkek.bereke.util.withLoadingDialog
import java.io.ByteArrayOutputStream

class CreateNoteViewModel(
    private val appData: AppData,
    private val rxTakePhoto: RxTakePhoto,
    private val notesRepository: NotesRepository
) : BaseViewModel(appData) {

    private var isFirstLaunch = true
    private val listSelectedImages = arrayListOf<LocalImage>()

    private val _selectedImages = MutableLiveData<List<LocalImage>>()
    val selectedImages: LiveData<List<LocalImage>> get() = _selectedImages

    private val _noteCreated = MutableLiveData<Boolean>()
    val noteCreated: LiveData<Boolean> get() = _noteCreated

    private val _draftCreated = MutableLiveData<Boolean>()
    val draftCreated: LiveData<Boolean> get() = _draftCreated

    private val _noteDraft = MutableLiveData<NoteDraftModel?>()
    val noteDraft: LiveData<NoteDraftModel?> get() = _noteDraft

    init {
        loadNoteDraft()
    }

    private fun loadNoteDraft() {
        compositeDisposable += notesRepository.loadNoteDraft()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    _noteDraft.postValue(null)
                },
                onSuccess = {
                    _noteDraft.postValue(it.draft)
                })
    }

    fun createNote(noteRequestBody: NoteRequestBody) {
        compositeDisposable += notesRepository.createNote(noteRequestBody)
            .flatMapCompletable { i -> getImageRequest(i.id) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _noteCreated.postValue(true)
            }
    }

    fun createNoteDraft(draft: NoteDraftModel) {
        compositeDisposable += notesRepository.createNoteDraft(draft)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = {
                    _draftCreated.postValue(true)
                })
    }


    fun showGallery() {
        compositeDisposable += rxTakePhoto.takeMultipleImages()
            .doOnSuccess {
                it.forEach { image ->
                    if (!isSameImage(image, listSelectedImages)){
                        listSelectedImages.add(image)
                    }
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedImages.postValue(listSelectedImages)
            }
    }

    fun removeSelectedImage(bitmap: LocalImage) {
        compositeDisposable += Completable.fromAction {
            listSelectedImages.apply {
                removeIf { x -> x.id == bitmap.id }
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                Log.e("SIZE", listSelectedImages.size.toString())
                _selectedImages.postValue(listSelectedImages)
            }
    }

    private fun getImageRequest(noteId: String): Completable {
        if (listSelectedImages.isNullOrEmpty()) {
            return Completable.complete()
        } else {
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    listSelectedImages.forEachIndexed { index, bitmap ->
                        val byteArray = ByteArrayOutputStream().let {
                            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            bitmap.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            it.toByteArray()
                        }

                        val body =
                            byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                        addFormDataPart(
                            "file$index",
                            "image$index.png",
                            body
                        )
                    }
                }
                .build()
            return notesRepository.uploadNoteImages(noteId, body).ignoreElement()
        }
    }

    private fun isSameImage(image: LocalImage, list: List<LocalImage>): Boolean {
        var isSame = false
        if (list.isNullOrEmpty()) isSame = false
        else {
            list.forEach {
                if (it.id == image.id) {
                    isSame = true
                    return@forEach
                }
            }
        }
        return isSame
    }
}