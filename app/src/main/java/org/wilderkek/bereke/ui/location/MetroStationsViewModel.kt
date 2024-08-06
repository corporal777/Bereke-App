package org.wilderkek.bereke.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.repo.location.LocationRepository
import org.wilderkek.bereke.model.response.MetroStationsModel
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain

class MetroStationsViewModel(
    private val appData: AppData,
    private val locationRepository: LocationRepository
) : BaseViewModel(appData) {

    private val listStations = arrayListOf<MetroStationsModel>()
    private val listSelectedStations = arrayListOf<String>()
    private var coordinates: Pair<String?, String?> = Pair(null, null)

    private val _metroStations = MutableLiveData<List<MetroStationsModel>?>()
    val metroStations: LiveData<List<MetroStationsModel>?> get() = _metroStations

    private val _selectedStations = MutableLiveData<List<String>>(listSelectedStations)
    val selectedStations: LiveData<List<String>> get() = _selectedStations


    fun loadMetroStations(city: String?, list: List<String>) {
        listSelectedStations.addAll(list)
        compositeDisposable += locationRepository.getMetroStations(city)
            .doOnSuccess { listStations.addAll(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedStations.postValue(listSelectedStations)
                _metroStations.postValue(listStations)
            }
    }

    fun searchStation(station: String) {
        if (station.isNullOrEmpty()) {
            _metroStations.postValue(listStations)
        } else {
            compositeDisposable += Maybe.fromCallable {
                listStations.filter { x -> x.name.contains(station, true) }
            }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    _metroStations.postValue(it)
                }
        }
    }

    fun selectStation(station: String) {
        if (listSelectedStations.contains(station)) {
            listSelectedStations.remove(station)
        } else {
            listSelectedStations.add(station)
        }
        _selectedStations.value = listSelectedStations
    }


    fun getSelectedStations() = listSelectedStations
    fun getSelectedStationCoords(): Pair<String?, String?> {
        if (!listSelectedStations.isNullOrEmpty()){
            val metro = listStations.find { x -> x.name == listSelectedStations.lastOrNull() }
            coordinates = Pair(metro?.lat, metro?.lng)
        }
        return coordinates
    }
}