package com.mtm.uber_mimic.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtm.uber_mimic.tools.location.LocationHelper
import com.mtm.uber_mimic.tools.location.exceptions.LocationPermissionException
import kotlinx.coroutines.launch

class RequestRideViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _locationViewState: MutableLiveData<LocationViewState> by lazy {
        MutableLiveData()
    }

    val locationViewState: LiveData<LocationViewState> = _locationViewState

    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val latLng = locationHelper.getLocation()
                _locationViewState.postValue(LocationViewState.Data(latLng))
            } catch (throwable: Throwable) {
                val error = if(throwable is LocationPermissionException)
                    LocationViewState.Error.Permission
                else
                    LocationViewState.Error.Unknown
                _locationViewState.postValue(error)
            }
        }
    }
}