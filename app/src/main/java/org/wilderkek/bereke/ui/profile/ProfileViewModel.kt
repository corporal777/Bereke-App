package org.wilderkek.bereke.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.wilderkek.bereke.R
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withDelay
import org.wilderkek.bereke.util.withLoadingDialog
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.di.repo.user.UserRepository
import org.wilderkek.bereke.model.response.UserModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import java.util.concurrent.TimeUnit

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData
) : BaseViewModel(appData) {

    private var isFirstLaunch = true
    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        compositeDisposable += userRepository.getCurrentUser().ignoreElement()
            .timeout(2000, TimeUnit.MILLISECONDS)
            .let {
                if (isFirstLaunch) {
                    isFirstLaunch = false
                    it.withLoadingDialog(loadingData)
                } else it
            }
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {}
            )
    }

    fun loadUser() {
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    _userData.postValue(null)
                },
                onNext = {
                    _userData.postValue(it.value)
                }
            )
    }

    fun logout() {
        compositeDisposable += authRepository.logout(appData.getUserId())
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {
                    appData.logOut()
                    appData.sendMessageSubject(R.string.you_logged_out_from_account)
                }
            )
    }


    fun isUserLoggedOut() = appData.isLoggedOut()
}