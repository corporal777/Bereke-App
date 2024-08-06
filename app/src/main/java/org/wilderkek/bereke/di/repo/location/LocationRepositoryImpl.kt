package org.wilderkek.bereke.di.repo.location

import io.reactivex.Maybe
import org.wilderkek.bereke.api.ApiService
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.model.response.LocationRequestModel
import org.wilderkek.bereke.model.response.LocationResponseModel
import org.wilderkek.bereke.model.response.MetroStationsModel
import org.wilderkek.bereke.model.response.SpecialityModel

class LocationRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    LocationRepository {


    override fun checkUserLocation(locationRequest: LocationRequestModel): Maybe<LocationResponseModel> {
        return apiService.checkUserLocation(locationRequest)
    }

    override fun findLocations(city: String): Maybe<List<LocationResponseModel>> {
        return apiService.findLocations(city)
    }

    override fun getMetroStations(city: String?): Maybe<List<MetroStationsModel>> {
        return apiService.getMetroStations(city?:"")
    }

    override fun findSpecialities(speciality: String): Maybe<List<SpecialityModel>> {
        return apiService.getSpecialities(speciality)
    }
}