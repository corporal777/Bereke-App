package org.wilderkek.bereke.ui.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.otunjargych.tamtam.model.request.ErrorResponse
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.util.toSingleEvent
import retrofit2.HttpException

open class BaseViewModel(private val appData: AppData) : ViewModel() {

    val progressData = MutableLiveData<Boolean>()
    val loadingData = MutableLiveData<Boolean>()
    val compositeDisposable = CompositeDisposable()


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage get() = _errorMessage.toSingleEvent()


    fun HttpException.getErrorResponse(): ErrorResponse? {
        return Gson().fromJson(
            this.response()?.errorBody()?.string(),
            ErrorResponse::class.java
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


    fun Completable.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onComplete: () -> Unit
    ): Disposable {
        return subscribe(
            Action(onComplete),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Single<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onSuccess),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Maybe<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onSuccess),
            createOnErrorConsumer(onError)
        )
    }

    fun <T> Observable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onNext),
            createOnErrorConsumer(onError)
        )
    }


    fun <T> Flowable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable {
        return subscribe(
            Consumer(onNext),
            createOnErrorConsumer(onError)
        )
    }

    private fun createOnErrorConsumer(onError: ((Throwable) -> Unit)?): Consumer<Throwable> {
        return Consumer {
            it.printStackTrace()
            if (onError != null) onError(it)
            if (it is java.lang.NullPointerException && it.message == "User is not authorized!") {
                _errorMessage.postValue("Необходимо войти в аккаунт")
            }
        }
    }

    fun clearErrorMessage() {
        this._errorMessage.value = null
    }

    fun isUserAuthorized() = appData.isUserAuthorized()
    fun getCurrentTown() = appData.getUserTown()
}