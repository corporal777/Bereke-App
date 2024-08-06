package org.wilderkek.bereke.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.di.repo.user.UserRepository
import org.wilderkek.bereke.model.request.LoginRequestBody
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.AuthValidateUtil
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withLoadingDialog
import org.wilderkek.bereke.util.isPhone
import org.wilderkek.bereke.util.isPhoneIsValid
import org.wilderkek.bereke.model.response.UserModel

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    appData: AppData
) : BaseViewModel(appData) {

    private val _enableBtnLogin = MutableLiveData(false)
    val enableBtnLogin: LiveData<Boolean> get() = _enableBtnLogin

    private val _loginError = MutableLiveData(false)
    val loginError: LiveData<Boolean> get() = _loginError

    private val _passwordError = MutableLiveData(false)
    val passwordError: LiveData<Boolean> get() = _passwordError

    private val _errorResponse = MutableLiveData(false)
    val errorResponse: LiveData<Boolean> get() = _errorResponse

    private val _successResponse = MutableLiveData<UserModel>()
    val successResponse: LiveData<UserModel> get() = _successResponse

    private var login = ""
    private var password = ""
    var fromNote = false
    var fromProfile = false

    fun login() {
        if (isDataValid()) {
            compositeDisposable += authRepository.login(LoginRequestBody(login, password))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeBy(
                    onError = {
                        it.printStackTrace()
                        _errorResponse.postValue(true)
                    }, onSuccess = {
                        _successResponse.postValue(it)
                    }
                )
        }
    }

    fun performChangeLogin(login: String) {
        this.login = login
        _loginError.postValue(false)
        _enableBtnLogin.postValue(!this.login.isNullOrEmpty())
    }

    fun performChangePassword(password: String) {
        this.password = password
        _passwordError.postValue(false)
        _enableBtnLogin.postValue(!this.password.isNullOrEmpty())
    }

    private fun isDataValid(): Boolean {
        val loginValid = if (isPhone(login)) isPhoneIsValid(login)
        else AuthValidateUtil.isValidEmail(login)
        val passwordValid = AuthValidateUtil.isValidPassword(password)
        _passwordError.postValue(!passwordValid)
        _loginError.postValue(!loginValid)
        return loginValid && passwordValid
    }
}