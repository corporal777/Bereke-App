package org.wilderkek.bereke.ui.profileSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.wilderkek.bereke.R
import org.wilderkek.bereke.util.IMAGE_MAX_SIZE_AVATAR
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withLoadingDialog
import org.wilderkek.bereke.util.rxtakephoto.ResultRotation
import org.wilderkek.bereke.util.rxtakephoto.RxTakePhoto
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.user.UserRepository
import org.wilderkek.bereke.model.response.UserModel
import org.wilderkek.bereke.ui.base.BaseViewModel

class ProfileSettingsViewModel(
    private val userRepository: UserRepository,
    private val rxTakePhoto: RxTakePhoto,
    private val appData: AppData
) : BaseViewModel(appData) {

    private val _enableBtnSave = MutableLiveData(false)
    val enableBtnSave: LiveData<Boolean> get() = _enableBtnSave

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _userUpdated = MutableLiveData<Boolean>()
    val userUpdated: LiveData<Boolean> get() = _userUpdated

    private var firstName = appData.getUser()?.firstName
    private var lastName = appData.getUser()?.lastName
    private var email = appData.getUser()?.contacts?.email
    private var phone = appData.getUser()?.contacts?.phone

    init {
        loadUser()
    }

    private fun loadUser() {
        compositeDisposable += Maybe.defer { Maybe.just(appData.getUser()) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _user.postValue(null)
                },
                onSuccess = {
                    _user.postValue(it)
                })
    }

    fun takeGalleryImage() = takePhoto(rxTakePhoto.takeGalleryImage())
    fun takeCameraImage() = takePhoto(rxTakePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
            .firstOrError()
            .flatMap {
                rxTakePhoto.crop(
                    resultRotation = it,
                    outputMaxWidth = IMAGE_MAX_SIZE_AVATAR,
                    outputMaxHeight = IMAGE_MAX_SIZE_AVATAR,
                    cropMode = CropImageView.CropMode.SQUARE
                )
            }
            .flatMap { userRepository.changeUserImage(it) }
            .doOnSuccess { appData.updateUserField { image = it.image } }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = {
                    appData.sendMessageSubject(R.string.user_image_success_updated)
                    _user.postValue(appData.getUser())
                }
            )
    }

    fun updateUser() {
        if (isDataValid()) {
            compositeDisposable += userRepository.updateUser(getDataToSave())
                .doOnSuccess { appData.updateUser(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                    },
                    onSuccess = {
                        appData.sendMessageSubject(R.string.data_success_updated)
                        _user.postValue(it)
                        _userUpdated.postValue(true)
                    })
        }
    }


    fun performChangeFirstName(name: String) {
        this.firstName = name
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangeLastName(lastName: String) {
        this.lastName = lastName
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangeEmail(email: String) {
        this.email = email
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangePhone(phone: String) {
        this.phone = phone
        _enableBtnSave.postValue(isDataValid())
    }

    private fun isDataValid(): Boolean {
        val nameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val loginValid = !appData.getUser()?.login.isNullOrBlank()
        return nameValid && lastNameValid && loginValid
    }

    private fun getDataToSave(): MutableMap<String, String?> {
        return mutableMapOf<String, String?>().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("email", email)
            put("phone", phone)
        }
    }
}