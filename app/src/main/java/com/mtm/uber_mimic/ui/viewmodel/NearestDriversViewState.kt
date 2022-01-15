package com.mtm.uber_mimic.ui.viewmodel

import com.mtm.uber_mimic.ui.models.DriverModel

sealed class NearestDriversViewState {
    object Loading : NearestDriversViewState()

    class Data(val drivers: List<DriverModel>) : NearestDriversViewState()

    sealed class Error : NearestDriversViewState() {
        object SourceMissing : Error()
        object UnknownError : Error()
    }
}
