package com.mtm.uber_mimic.ui.viewmodel

import com.google.android.gms.maps.model.LatLng

sealed class CurrentLocationViewState {

    object Loading : CurrentLocationViewState()

    class Data(val latLng: LatLng) : CurrentLocationViewState()

    sealed class Error: CurrentLocationViewState() {
        object Permission : Error()

        object Unknown : Error()
    }
}
