package org.wilderkek.bereke.model.response

data class LocationRequestModel(
    val lat: Double,
    val lon: Double
)

data class LocationResponseModel(
    val coordinates: LocationCoordinatesModel,
    val city: String,
    val district: String,
    val country: String
)

data class LocationCoordinatesModel(
    val lat: String,
    val lon: String
)


data class MetroStationsModel(
    val name: String,
    val color: List<String>,
    val lat: String,
    val lng: String
)