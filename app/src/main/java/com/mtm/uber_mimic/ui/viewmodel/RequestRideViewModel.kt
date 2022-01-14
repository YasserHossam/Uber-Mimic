package com.mtm.uber_mimic.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtm.uber_mimic.domain.usecase.GetSourcesUseCase
import com.mtm.uber_mimic.tools.location.LocationHelper
import com.mtm.uber_mimic.tools.location.exceptions.LocationPermissionException
import com.mtm.uber_mimic.ui.models.mappers.LocationModelMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class RequestRideViewModel(
    private val locationHelper: LocationHelper,
    private val getSourcesUseCase: GetSourcesUseCase,
    private val locationModelMapper: LocationModelMapper
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
                _sourcesViewState.postValue(LocationViewState.Data(sources))
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException)
                    _sourcesViewState.postValue(LocationViewState.Error)
            }
        }
    }

}