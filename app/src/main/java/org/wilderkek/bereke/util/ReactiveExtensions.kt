package org.wilderkek.bereke.util

import androidx.lifecycle.MutableLiveData
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer


/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Completable]
 * */
fun Completable.performOnBackgroundOutOnMain(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Flowable]
 * */
fun <T> Flowable<T>.performOnBackgroundOutOnMain(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Single]
 * */
fun <T> Single<T>.performOnBackgroundOutOnMain(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Maybe]
 * */
fun <T> Maybe<T>.performOnBackgroundOutOnMain(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Observable]
 * */
fun <T> Observable<T>.performOnBackgroundOutOnMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to add a Disposable to a CompositeDisposable
 */
@Deprecated("Use rxkotlin", ReplaceWith("compositeDisposable += this"))
fun Disposable.call(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

/**
 * Extension function to subscribe on the background thread for a Flowable
 * */
fun <T> Flowable<T>.performOnBackground(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Maybe
 * */
fun <T> Maybe<T>.performOnBackground(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Completable
 * */
fun Completable.performOnBackground(): Completable {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Observable
 * */
fun <T> Observable<T>.performOnBackground(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the main thread for a Observable
 * */
fun <T> Observable<T>.performOnMain(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun Completable.withLoadingDialog(baseView: MutableLiveData<Boolean>): Completable {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Observable<T>.withLoadingDialog(baseView: MutableLiveData<Boolean>): Observable<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideLoading(baseView, loadingDisposable)
            }
        }
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnTerminate(getHideLoadingAction(baseView, loadingDisposable))
}

fun <T> Single<T>.withLoadingDialog(baseView: MutableLiveData<Boolean>): Single<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))

}

fun <T> Maybe<T>.withLoadingDialog(baseView: MutableLiveData<Boolean>): Maybe<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Flowable<T>.withLoadingDialog(baseView: MutableLiveData<Boolean>): Flowable<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideLoading(baseView, loadingDisposable)
            }
        }
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnTerminate(getHideLoadingAction(baseView, loadingDisposable))
}


fun <T> Single<T>.withDelay(time: Long): Single<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Maybe<T>.withDelay(time: Long): Maybe<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun Completable.withDelay(time: Long): Completable {
    return delay(time, TimeUnit.MILLISECONDS)
}


private fun getLoadingDisposable(baseView: MutableLiveData<Boolean>): Disposable {
    //baseView.postValue(true)
    return Completable.complete()
        //.delay(300, TimeUnit.MILLISECONDS, Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        //.doOnComplete { baseView.postValue(true) }
        .doOnSubscribe { baseView.postValue(true) }
        .doOnDispose { baseView.postValue(false) }
        .subscribe()
}



private fun getHideLoadingAction(baseView: MutableLiveData<Boolean>, loading: Disposable) = Action {
    hideLoading(baseView, loading)
}


private fun <T> getHideLoadingConsumer(baseView: MutableLiveData<Boolean>, loading: Disposable) =
    Consumer<T> {
        hideLoading(baseView, loading)
    }

private fun hideLoading(baseView: MutableLiveData<Boolean>, loading: Disposable) {
    if (loading.isDisposed) baseView.postValue(false)
    else loading.dispose()
}


