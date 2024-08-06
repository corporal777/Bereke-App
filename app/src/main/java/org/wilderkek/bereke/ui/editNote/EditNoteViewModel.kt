package org.wilderkek.bereke.ui.editNote

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.note.NotesRepository
import org.wilderkek.bereke.model.request.HouseNoteAdditionalData
import org.wilderkek.bereke.model.request.NoteDraftModel
import org.wilderkek.bereke.model.request.WorkNoteAdditionalData
import org.wilderkek.bereke.model.response.NoteDetailModel
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.response.NoteRequestBody
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.ui.detailNote.data.NoteDetailData
import org.wilderkek.bereke.util.fromJson
import org.wilderkek.bereke.util.imagepicker.model.LocalImage
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.rxtakephoto.RxTakePhoto
import org.wilderkek.bereke.util.withLoadingDialog
import java.io.ByteArrayOutputStream

class EditNoteViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository,
    private val rxTakePhoto: RxTakePhoto,
) : BaseViewModel(appData) {

    var noteId = ""
    private val listImages = arrayListOf<LocalImage>()

    private val _selectedImages = MutableLiveData<List<LocalImage>?>()
    val selectedImages: LiveData<List<LocalImage>?> get() = _selectedImages

    private val _userNote = MutableLiveData<NoteDraftModel>()
    val userNote: LiveData<NoteDraftModel> get() = _userNote

    private val _noteUpdated = MutableLiveData<Boolean>()
    val noteUpdated: LiveData<Boolean> get() = _noteUpdated


    fun loadUserNote(noteId: String) {
        compositeDisposable += notesRepository.getNoteDetail(noteId)
            .doOnSuccess { listImages.addAll(rxTakePhoto.toLocalImage(it.note.images)) }
            .map { transformNoteToDraft(it) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .subscribeSimple {
                _userNote.postValue(it)
                _selectedImages.postValue(listImages)
            }
    }

    fun updateUserNote(noteRequestBody: NoteRequestBody) {
        compositeDisposable += notesRepository.updateNote(noteId, noteRequestBody)
            .flatMapCompletable { i -> getImageRequest(i.id) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _noteUpdated.postValue(true)
            }

    }

    fun showGallery() {
        compositeDisposable += rxTakePhoto.takeMultipleImages()
            .doOnSuccess {
                it.forEach { image -> if (!isSameImage(image, listImages)) listImages.add(image) }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedImages.postValue(listImages)
            }
    }

    fun removeSelectedImage(image: LocalImage) {
        compositeDisposable += Completable.fromAction {
            listImages.apply { removeIf { x -> x.id == image.id } }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedImages.postValue(listImages)
            }
    }

    private fun transformNoteToDraft(noteDetail: NoteDetailModel): NoteDraftModel {
        var additionalData = ""
        when (noteDetail.note.category) {
            NoteModel.Category.WORK -> {
                additionalData =
                    noteDetail.additionalData.fromJson<WorkNoteAdditionalData>()?.toDraft() ?: ""
            }
            NoteModel.Category.HOUSE -> {
                additionalData =
                    noteDetail.additionalData.fromJson<HouseNoteAdditionalData>()?.toDraft() ?: ""
            }
        }
        return NoteDraftModel(
            noteDetail.note.name,
            noteDetail.note.description,
            noteDetail.note.salary,
            noteDetail.note.getCategoryValue(),
            noteDetail.note.contacts,
            noteDetail.note.address,
            additionalData
        )
    }

    private fun getImageRequest(noteId: String): Completable {
        if (listImages.isNullOrEmpty()) {
            return Completable.complete()
        } else {
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    listImages.forEachIndexed { index, image ->
                        val byteArray = ByteArrayOutputStream().let {
                            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            image.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
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