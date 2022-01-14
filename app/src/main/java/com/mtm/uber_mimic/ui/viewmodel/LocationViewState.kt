package com.mtm.uber_mimic.ui.viewmodel

import com.google.android.gms.maps.model.LatLng

sealed class LocationViewState {

    object Loading : LocationViewState()

    class Data(val latLng: LatLng) : LocationViewState()

    sealed class Error: LocationViewState() {
        object Permission : Error()

        object Unknown : Error()
    }
}
