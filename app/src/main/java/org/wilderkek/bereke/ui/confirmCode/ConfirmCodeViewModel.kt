package org.wilderkek.bereke.ui.confirmCode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.auth.AuthRepository
import org.wilderkek.bereke.model.request.RegisterLoginModel
import org.wilderkek.bereke.model.request.VerifyLoginModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withLoadingDialog
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class ConfirmCodeViewModel(
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BaseViewModel(appData) {

    private val timerCompositeDisposable = CompositeDisposable()

    private val _enableBtnConfirm = MutableLiveData<Boolean>(false)
    val enableBtnConfirm: LiveData<Boolean> get() = _enableBtnConfirm

    private val _enableBtnSendAgain = MutableLiveData<Boolean>(false)
    val enableBtnSendAgain: LiveData<Boolean> get() = _enableBtnSendAgain

    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int> get() = _timer

    private val _codeSuccess = MutableLiveData<Boolean?>()
    val codeSuccess: LiveData<Boolean?> get() = _codeSuccess

    private var verifyCode = ""
    private var login = ""
    private var loginType = ""

    init {
        compositeDisposable += timerCompositeDisposable
        startTimer()
    }


    fun confirmCode() {
        compositeDisposable += authRepository.checkVerifyCode(VerifyLoginModel(login, verifyCode))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    if (it is HttpException) {
                        try {
                            val error = it.getErrorResponse()
                            if (error?.message == "Verify code is not correct!") _codeSuccess.postValue(
                                false
                            )
                        } catch (e: Exception) {
                        }
                    }
                },
                onComplete = {
                    _codeSuccess.postValue(true)
                })
    }

    fun sendCodeAgain() {
        compositeDisposable += authRepository.sendVerifyCode(login)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                startTimer()
            }
    }

    private fun startTimer() {
        _enableBtnSendAgain.postValue(false)
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                _enableBtnSendAgain.postValue(timeLeft < 0)
                _timer.postValue(timeLeft)
                if (timeLeft < 0) timerCompositeDisposable.clear()
            }, {
                it.printStackTrace()
            })
    }


    fun performChangeCode(code: String) {
        this.verifyCode = code
        _codeSuccess.postValue(null)
        _enableBtnConfirm.postValue(!verifyCode.isNullOrBlank())
    }

    fun setLoginAndType(login: String, type: ConfirmCodeFragment.ConfirmType) {
        this.login = login
        this.loginType = if (type == ConfirmCodeFragment.ConfirmType.EMAIL) "email"
        else "phone"
    }

    companion object {
        const val TIMER_SECONDS_COUNT = 60
    }
}