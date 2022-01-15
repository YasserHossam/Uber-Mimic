package com.mtm.uber_mimic.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtm.uber_mimic.domain.usecase.GetLocationsUseCase
import com.mtm.uber_mimic.domain.usecase.GetNearestDriversUseCase
import com.mtm.uber_mimic.tools.location.LocationHelper
import com.mtm.uber_mimic.tools.location.exceptions.LocationPermissionException
import com.mtm.uber_mimic.ui.models.LocationModel
import com.mtm.uber_mimic.ui.models.mappers.DriverModelMapper
import com.mtm.uber_mimic.ui.models.mappers.LocationModelMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

class RequestRideViewModel(
    private val locationHelper: LocationHelper,
    private val getSourcesUseCase: GetLocationsUseCase,
    private val getDestinationsUseCase: GetLocationsUseCase,
    private val getNearestDriversUseCase: GetNearestDriversUseCase,
    private val locationModelMapper: LocationModelMapper,
    private val driverModelMapper: DriverModelMapper
) : ViewModel() {

    private val _currentLocationViewState: MutableLiveData<CurrentLocationViewState> by lazy {
        MutableLiveData()
    }
    val currentLocationViewState: LiveData<CurrentLocationViewState> = _currentLocationViewState

    fun getCurrentLocation() {
        _currentLocationViewState.postValue(CurrentLocationViewState.Loading)
        viewModelScope.launch {
            try {
                val latLng = locationHelper.getLocation()
                _currentLocationViewState.postValue(CurrentLocationViewState.Data(latLng))
            } catch (throwable: Throwable) {
                val error = if (throwable is LocationPermissionException)
                    CurrentLocationViewState.Error.Permission
                else
                    CurrentLocationViewState.Error.Unknown
                _currentLocationViewState.postValue(error)
            }
        }
    }

    private val _sourcesViewState: MutableLiveData<LocationViewState> by lazy {
        MutableLiveData()
    }

    val sourcesViewState: LiveData<LocationViewState> = _sourcesViewState

    private var getSourcesJob: Job? = null

    fun getSources(keyword: String = "") {
        getSourcesJob?.cancel()
        _sourcesViewState.postValue(LocationViewState.Loading)
        getSourcesJob = viewModelScope.launch {
            try {
                val sources = locationModelMapper.transform(getSourcesUseCase(keyword))
                _sourcesViewState.postValue(LocationViewState.Data(sources, LocationType.SOURCE))
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException)
                    _sourcesViewState.postValue(LocationViewState.Error(LocationType.SOURCE))
            }
        }
    }

    private val _destinationsViewState: MutableLiveData<LocationViewState> by lazy {
        MutableLiveData()
    }

    val destinationsViewState: LiveData<LocationViewState> = _destinationsViewState

    private var getDestinationsJob: Job? = null

    fun getDestinations(keyword: String = "") {
        getDestinationsJob?.cancel()
        _destinationsViewState.postValue(LocationViewState.Loading)
        getDestinationsJob = viewModelScope.launch {
            try {
                val sources = locationModelMapper.transform(getDestinationsUseCase(keyword))
                _destinationsViewState.postValue(
                    LocationViewState.Data(
                        sources,
                        LocationType.DESTINATION
                    )
                )
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException)
                    _destinationsViewState.postValue(LocationViewState.Error(LocationType.DESTINATION))
            }
        }
    }

    private val _driversViewState: MutableLiveData<NearestDriversViewState> by lazy {
        MutableLiveData()
    }

    val driversViewState: LiveData<NearestDriversViewState> = _driversViewState

    private var getDriversJob: Job? = null

    private var selectedSource: LocationModel? = null

    fun selectSource(locationModel: LocationModel) {
        selectedSource = locationModel
    }

    fun getNearestDrivers() {
        val (_, _, lat, lng) = if (selectedSource == null) {
            _driversViewState.postValue(NearestDriversViewState.Error.SourceMissing)
            return
        } else {
            selectedSource!!
        }
        getDriversJob?.cancel()
        _driversViewState.postValue(NearestDriversViewState.Loading)
        getDriversJob = viewModelScope.launch {
            try {
                val drivers = driverModelMapper.transform(getNearestDriversUseCase(lat, lng))
                _driversViewState.postValue(NearestDriversViewState.Data(drivers))
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException)
                    _driversViewState.postValue(NearestDriversViewState.Error.UnknownError)
            }
        }
    }

}