package org.wilderkek.bereke.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.di.repo.user.UserRepository
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.call
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.toSingleEvent
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData
) : BaseViewModel(appData) {

    private val _userTown = MutableLiveData<String>()
    val userTown: LiveData<String> get() = _userTown

    private val _tokenUpdated = MutableLiveData<Boolean>(false)
    val tokenUpdated: LiveData<Boolean> get() = _tokenUpdated

    private val _snackBarMessage = MutableLiveData<String>()
    val snackBarMessage get() = _snackBarMessage.toSingleEvent()


    init {
        loadToken()
        observeSnackBarMessage()
    }

    private fun loadToken() {
        compositeDisposable += authRepository.getToken(appData.getDeviceUID())
            .timeout(2000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    observeTown()
                    _tokenUpdated.postValue(true)
                    observeUserChange()
                },
                onSuccess = {
                    observeTown()
                    _tokenUpdated.postValue(true)
                    observeUserChange()
                })
    }

    private fun observeTown() {
        compositeDisposable += Maybe.just(appData.getUserTown())
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _userTown.postValue(it)
            }
    }

    private fun observeUserChange() {
        compositeDisposable += appData.userIdChangeSubject
            .subscribeSimple {
                authRepository.sendFcmToken()
                    .subscribeSimple { Log.e("FCM TOKEN: ", "was sent successfully!") }
                    .call(compositeDisposable)
            }
    }

    private fun observeSnackBarMessage(){
        compositeDisposable += appData.messageSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _snackBarMessage.postValue(it)
            }
    }


}