package org.wilderkek.bereke.ui.town

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.location.LocationRepository
import org.wilderkek.bereke.model.response.LocationRequestModel
import org.wilderkek.bereke.model.response.LocationResponseModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.withDelay
import org.wilderkek.bereke.util.withLoadingDialog
import java.util.concurrent.TimeUnit

class TownViewModel(
    private val appData: AppData,
    private val locationRepository: LocationRepository,
    private val locationProviderClient: FusedLocationProviderClient
) : BaseViewModel(appData) {

    private val _userLocation = MutableLiveData<LocationResponseModel?>()
    val userLocation: LiveData<LocationResponseModel?> get() = _userLocation

    private val _townSavedSuccess = MutableLiveData<Boolean>()
    val townSavedSuccess: LiveData<Boolean> get() = _townSavedSuccess

    var userTown = ""

    fun initLocation(request: Observable<Location>) {
        compositeDisposable += request.firstElement()
            .flatMap {
                locationRepository.checkUserLocation(
                    LocationRequestModel(
                        it.latitude,
                        it.longitude
                    )
                )
            }
            .timeout(10000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    _userLocation.postValue(null)
                },
                onSuccess = {
                    if (!it.city.isNullOrEmpty()) userTown = it.city
                    _userLocation.postValue(it)
                })

    }

    fun saveUserCity() {
        compositeDisposable += Completable.fromAction {
            appData.initUserTown(userTown)
        }
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _townSavedSuccess.postValue(true)
            }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(request: Observable<Boolean>): Maybe<Location> {
        return request.firstElement()
            .flatMap { isGranted ->
                if (isGranted) {
                    Maybe.create<Location> { emitter ->
                        val locationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setNumUpdates(1)

                        val callback = object : LocationCallback() {
                            override fun onLocationResult(location: LocationResult) {
                                location.lastLocation?.let { emitter.onSuccess(it) }
                            }
                        }
                        locationProviderClient.requestLocationUpdates(
                            locationRequest,
                            callback,
                            Looper.getMainLooper()
                        )
                            .addOnFailureListener {
                                emitter.onError(it)
                                it.printStackTrace()
                            }

                        emitter.setCancellable {
                            locationProviderClient.removeLocationUpdates(callback)
                        }
                    }
                } else Maybe.empty()
            }
    }
}