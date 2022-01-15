package com.mtm.uber_mimic.ui.viewmodel

import com.mtm.uber_mimic.ui.models.LocationModel

sealed class LocationViewState {

    object Loading : LocationViewState()

    data class Data(val locations: List<LocationModel>, val type: LocationType) :
        LocationViewState()

    data class Error(val type: LocationType) : LocationViewState()
}

enum class LocationType {
    SOURCE,
    DESTINATION
}
