package org.wilderkek.bereke.ui.town

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.location.LocationRepository
import org.wilderkek.bereke.model.response.LocationResponseModel
import org.wilderkek.bereke.model.response.SpecialityModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain

class CitiesViewModel(
    private val appData: AppData,
    private val locationRepository: LocationRepository
) : BaseViewModel(appData) {

    private val _cities = MutableLiveData<List<LocationResponseModel>>()
    val cities: LiveData<List<LocationResponseModel>> get() = _cities

    private val _specialities = MutableLiveData<List<SpecialityModel>>()
    val specialities: LiveData<List<SpecialityModel>> get() = _specialities

    fun findCities(city: String) {
        compositeDisposable += locationRepository.findLocations(city)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { _cities.postValue(it) }
            )
    }

    fun findSpecialities(spec: String) {
        compositeDisposable += locationRepository.findSpecialities(spec)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { _specialities.postValue(it) }
            )
    }


}