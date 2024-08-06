package org.wilderkek.bereke.ui.profileSettings.changeLogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.model.request.ErrorResponse
import org.wilderkek.bereke.util.AuthValidateUtil
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withLoadingDialog
import org.wilderkek.bereke.util.isPhone
import org.wilderkek.bereke.util.isPhoneIsValid
import org.wilderkek.bereke.R
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.ui.base.BaseViewModel
import retrofit2.HttpException

class ChangeLoginViewModel(
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BaseViewModel(appData) {


    private var newLogin = ""

    private val _loginError = MutableLiveData<Int?>()
    val loginError: LiveData<Int?> get() = _loginError

    private val _loginUpdated = MutableLiveData<Boolean>()
    val loginUpdated: LiveData<Boolean> get() = _loginUpdated


    fun updateLogin() {
        if (isLoginValid()) {
            compositeDisposable += authRepository.checkIfLoginExists(newLogin)
                .andThen(authRepository.updateUserLogin(getNewLogin()))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeSimple(
                    onError = { catchError(it) },
                    onComplete = {
                        appData.updateUserField { this.login = newLogin }
                        _loginUpdated.postValue(true)
                    }
                )
        } else _loginError.value = R.string.login_incorrect
    }

    private fun isLoginValid(): Boolean {
        var valid = true
        if (isPhone(newLogin)) {
            valid = isPhoneIsValid(newLogin)
        } else if (!isPhone(newLogin)) {
            valid = AuthValidateUtil.isValidEmail(newLogin)
        } else valid = false
        return valid
    }

    fun performChangeLogin(login: String) {
        this.newLogin = login
        _loginError.value = null
    }

    private fun catchError(it: Throwable) {
        it.printStackTrace()
        if (it is HttpException) {
            val error = Gson().fromJson(
                it.response()?.errorBody()?.string(),
                ErrorResponse::class.java
            )
            if (error.message == "Login is not free!") {
                _loginError.postValue(R.string.login_not_free_error)
            }
        }

    }

    private fun getNewLogin() = mapOf("login" to newLogin)

}