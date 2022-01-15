package com.mtm.uber_mimic.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mtm.uber_mimic.domain.usecase.GetCurrentLocationUseCase
import com.mtm.uber_mimic.domain.usecase.GetLocationsUseCase
import com.mtm.uber_mimic.domain.usecase.GetNearestDriversUseCase
import com.mtm.uber_mimic.ui.helper.PermissionHelper
import com.mtm.uber_mimic.ui.models.LocationModel
import com.mtm.uber_mimic.ui.models.mappers.DriverModelMapper
import com.mtm.uber_mimic.ui.models.mappers.LocationModelMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RequestRideViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSourcesUseCase: GetLocationsUseCase,
    private val getDestinationsUseCase: GetLocationsUseCase,
    private val getNearestDriversUseCase: GetNearestDriversUseCase,
    private val locationModelMapper: LocationModelMapper,
    private val driverModelMapper: DriverModelMapper,
    private val permissionHelper: PermissionHelper
) : ViewModel() {

    private val _viewState: MutableLiveData<RequestRideViewState> by lazy {
        MutableLiveData()
    }
    val requestRideViewState: LiveData<RequestRideViewState> = _viewState

    fun getCurrentLocation() {
        _viewState.postValue(RequestRideViewState.Loading)
        viewModelScope.launch {
            try {
                if (permissionHelper.isLocationPermissionsGranted()) {
                    val latLng = getCurrentLocationUseCase()
                    val googleLatLang = LatLng(latLng.lat, latLng.lng)
                    val state = RequestRideViewState.CurrentLocationData(googleLatLang)
                    _viewState.postValue(state)
                } else {
                    val state = RequestRideViewState.Error.CurrentLocationError.Permission
                    _viewState.postValue(state)
                }
            } catch (throwable: Throwable) {
                val state = RequestRideViewState.Error.CurrentLocationError.Unknown
                _viewState.postValue(state)
            }
        }
    }

    private var getSourcesJob: Job? = null

    fun getSources(keyword: String = "") {
        getSourcesJob?.cancel()
        _viewState.postValue(RequestRideViewState.Loading)
        getSourcesJob = viewModelScope.launch {
            try {
                val sources = locationModelMapper.transform(getSourcesUseCase(keyword))
                val state = RequestRideViewState.LocationsData(sources, LocationType.SOURCE)
                _viewState.postValue(state)
            } catch (throwable: Throwable) {
                val state = RequestRideViewState.Error.LocationError(LocationType.SOURCE)
                _viewState.postValue(state)
            }
        }
    }

    private var getDestinationsJob: Job? = null

    fun getDestinations(keyword: String = "") {
        getDestinationsJob?.cancel()
        _viewState.postValue(RequestRideViewState.Loading)
        getDestinationsJob = viewModelScope.launch {
            try {
                if (permissionHelper.isLocationPermissionsGranted()) {
                    val sources = locationModelMapper.transform(getDestinationsUseCase(keyword))
                    val state =
                        RequestRideViewState.LocationsData(sources, LocationType.DESTINATION)
                    _viewState.postValue(state)
                } else {
                    val state = RequestRideViewState.Error.CurrentLocationError.Unknown
                    _viewState.postValue(state)
                }
            } catch (throwable: Throwable) {
                val state = RequestRideViewState.Error.LocationError(LocationType.DESTINATION)
                _viewState.postValue(state)
            }
        }
    }

    private var getDriversJob: Job? = null

    private var selectedSource: LocationModel? = null

    fun selectSource(locationModel: LocationModel) {
        selectedSource = locationModel
    }

    fun getNearestDrivers() {
        val (_, _, lat, lng) = if (selectedSource == null) {
            val state = RequestRideViewState.Error.NearestDriverError.SourceMissing
            _viewState.postValue(state)
            return
        } else
            selectedSource!!
        getDriversJob?.cancel()
        _viewState.postValue(RequestRideViewState.Loading)
        getDriversJob = viewModelScope.launch {
            try {
                val drivers = driverModelMapper.transform(getNearestDriversUseCase(lat, lng))
                _viewState.postValue(RequestRideViewState.NearestDriverData(drivers))
            } catch (throwable: Throwable) {
                val state = RequestRideViewState.Error.NearestDriverError.UnknownError
                _viewState.postValue(state)
            }
        }
    }
}