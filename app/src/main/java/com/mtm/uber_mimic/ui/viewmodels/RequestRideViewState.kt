package com.mtm.uber_mimic.ui.viewmodels

import com.google.android.gms.maps.model.LatLng
import com.mtm.uber_mimic.ui.models.DriverModel
import com.mtm.uber_mimic.ui.models.LocationModel

sealed class RequestRideViewState {

    object Loading : RequestRideViewState()

    data class CurrentLocationData(val latLng: LatLng) : RequestRideViewState()

    data class LocationsData(val locations: List<LocationModel>, val type: LocationType) :
        RequestRideViewState()

    data class NearestDriverData(val drivers: List<DriverModel>) : RequestRideViewState()

    sealed class Error : RequestRideViewState() {
        sealed class CurrentLocationError : Error() {
            object Permission : CurrentLocationError()

            object Unknown : CurrentLocationError()
        }

        data class LocationError(val type: LocationType) : Error()

        sealed class NearestDriverError : Error() {
            object SourceMissing : NearestDriverError()
            object UnknownError : NearestDriverError()
        }
    }
}
