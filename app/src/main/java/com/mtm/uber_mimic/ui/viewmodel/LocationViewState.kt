package com.mtm.uber_mimic.ui.viewmodel

import com.mtm.uber_mimic.ui.models.LocationModel

sealed class LocationViewState {

    object Loading : LocationViewState()

    class Data(val locations: List<LocationModel>) : LocationViewState()

    object Error : LocationViewState()
}
