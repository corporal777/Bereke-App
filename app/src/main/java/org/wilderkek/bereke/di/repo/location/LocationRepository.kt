package org.wilderkek.bereke.di.repo.location

import io.reactivex.Maybe
import org.wilderkek.bereke.model.response.LocationRequestModel
import org.wilderkek.bereke.model.response.LocationResponseModel
import org.wilderkek.bereke.model.response.MetroStationsModel
import org.wilderkek.bereke.model.response.SpecialityModel

interface LocationRepository {
    fun checkUserLocation(locationRequest: LocationRequestModel): Maybe<LocationResponseModel>
    fun findLocations(city: String): Maybe<List<LocationResponseModel>>
    fun findSpecialities(speciality: String): Maybe<List<SpecialityModel>>
    fun getMetroStations(city: String?) : Maybe<List<MetroStationsModel>>
}