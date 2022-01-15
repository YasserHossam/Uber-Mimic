package com.mtm.uber_mimic.ui.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.mtm.uber_mimic.R
import com.mtm.uber_mimic.databinding.ActivityRequestRideBinding
import com.mtm.uber_mimic.ui.*
import com.mtm.uber_mimic.ui.adapters.LocationAdapter
import com.mtm.uber_mimic.ui.viewmodels.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope


class RequestRideActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRequestRideBinding

    private val viewModel: RequestRideViewModel by viewModel()

    private val sourcesAdapter: LocationAdapter by lazy {
        LocationAdapter {
            viewModel.selectSource(it)
            binding.editSource.apply {
                stopWatchingSourceField()
                setText(it.name)
                setSelection(length())
                binding.editDestination.requestFocus()
            }
        }
    }

    private val destinationsAdapter: LocationAdapter by lazy {
        LocationAdapter {
            binding.editDestination.apply {
                stopWatchingDestinationField()
                setText(it.name)
                setSelection(length())
                binding.ivSideMenu.show()
                binding.ivBack.gone()
                binding.recyclerLocation.hide()
                closeKeyboard()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        initListeners()
        initObservables()
        initMap()
        initRecycler()

    }

    override fun onBackPressed() {
        if (binding.ivBack.isVisible) {
            binding.ivBack.gone()
            binding.ivSideMenu.show()
            binding.recyclerLocation.gone()
        } else
            super.onBackPressed()
    }

    private fun initListeners() {
        binding.ivSideMenu.setOnClickListener {
            binding.drawerView.openDrawer(GravityCompat.START)
        }

        binding.ivBack.setOnClickListener {
            binding.ivSideMenu.show()
            binding.ivBack.gone()
            binding.recyclerLocation.hide()
            closeKeyboard()
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_your_trips -> YourTripsActivity.start(this@RequestRideActivity)
                R.id.nav_settings -> SettingsActivity.start(this@RequestRideActivity)
            }
            return@setNavigationItemSelectedListener true
        }

        binding.editSource.setOnClickListener {
            if (it.isFocused) {
                startWatchingSourceField()
                getLocations(it, binding.editSource.text.toString())
            } else
                stopWatchingSourceField()
        }

        binding.editDestination.setOnClickListener {
            if (it.isFocused) {
                startWatchingDestinationField()
                getLocations(it, binding.editDestination.text.toString())
            } else
                stopWatchingDestinationField()
        }

        binding.editSource.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                startWatchingSourceField()
                getLocations(view, binding.editSource.text.toString())
            } else
                stopWatchingSourceField()
        }

        binding.editDestination.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                startWatchingDestinationField()
                getLocations(view, binding.editDestination.text.toString())
            } else
                stopWatchingDestinationField()
        }

        binding.btnRequestRide.setOnClickListener {
            viewModel.getNearestDrivers()
        }
    }

    private var sourceWatchJob: Job? = null
    private fun startWatchingSourceField() {
        sourceWatchJob?.cancel()
        sourceWatchJob = binding.editSource.textChanges()
            .debounce(500)
            .onEach { getLocations(binding.editSource, binding.editSource.text.toString()) }
            .launchIn(lifecycleScope)
    }

    private fun stopWatchingSourceField() {
        sourceWatchJob?.cancel()
    }

    private var destinationWatchJob: Job? = null
    private fun startWatchingDestinationField() {
        destinationWatchJob?.cancel()
        destinationWatchJob = binding.editDestination.textChanges()
            .debounce(500)
            .onEach {
                getLocations(
                    binding.editDestination,
                    binding.editDestination.text.toString()
                )
            }
            .launchIn(lifecycleScope)
    }

    private fun stopWatchingDestinationField() {
        destinationWatchJob?.cancel()
    }

    private fun initObservables() {
        binding.layoutTouchInterceptor.isViewTouchedLiveData.observe(this) { isTouched ->
            if (isTouched) {
                binding.ivSideMenu.show()
                binding.ivBack.gone()
                binding.recyclerLocation.hide()
                closeKeyboard()
            }
        }
        viewModel.requestRideViewState.observe(this) {
            setViewState(it)
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            mMap = it
            viewModel.getCurrentLocation()
        }
    }

    private fun initRecycler() {
        binding.recyclerLocation.layoutManager = LinearLayoutManager(this)
    }

    private fun getLocations(view: View, currentQuery: String) {
        binding.ivBack.show()
        binding.ivSideMenu.gone()
        if (view.id == binding.editSource.id)
            viewModel.getSources(currentQuery)
        else if (view.id == binding.editDestination.id)
            viewModel.getDestinations(currentQuery)
    }

    private fun setViewState(viewState: RequestRideViewState) {
        hideLoadingState()
        when (viewState) {
            is RequestRideViewState.CurrentLocationData -> setCurrentLocationDataState(viewState)
            is RequestRideViewState.LocationsData -> setLocationDataState(viewState)
            is RequestRideViewState.NearestDriverData -> setDriversDataState(viewState)
            is RequestRideViewState.Error -> setErrorState(viewState)
            is RequestRideViewState.Loading -> setLoadingState()
        }
    }

    private fun setCurrentLocationDataState(viewState: RequestRideViewState.CurrentLocationData) {
        mMap.addMarker(MarkerOptions().position(viewState.latLng).title("Your Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewState.latLng, 16f))
    }

    private fun setLocationDataState(locationViewState: RequestRideViewState.LocationsData) {
        if (locationViewState.locations.isEmpty())
            return
        binding.recyclerLocation.show()
        if (locationViewState.type == LocationType.SOURCE) {
            sourcesAdapter.submitList(locationViewState.locations)
            binding.recyclerLocation.adapter = sourcesAdapter
        } else if (locationViewState.type == LocationType.DESTINATION) {
            destinationsAdapter.submitList(locationViewState.locations)
            binding.recyclerLocation.adapter = destinationsAdapter
        }
    }

    private fun setDriversDataState(viewState: RequestRideViewState.NearestDriverData) {
        if (viewState.drivers.isEmpty())
            return
        val drivers = viewState.drivers.map { it.toString() }
        val message = StringBuilder().apply {
            append("Nearest Drivers: \n\n\n")
            append(drivers.joinToString(separator = "\n\n"))
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setErrorState(viewState: RequestRideViewState.Error) {
        val errorMessage = when (viewState) {
            RequestRideViewState.Error.CurrentLocationError.Permission ->
                getString(R.string.permission_error)

            RequestRideViewState.Error.CurrentLocationError.Unknown ->
                getString(R.string.get_current_location_error)

            is RequestRideViewState.Error.LocationError -> {
                if (viewState.type == LocationType.SOURCE)
                    getString(R.string.get_sources_error)
                else
                    getString(R.string.get_destinations_error)
            }

            RequestRideViewState.Error.NearestDriverError.SourceMissing ->
                getString(R.string.source_missing_error)

            RequestRideViewState.Error.NearestDriverError.UnknownError ->
                getString(R.string.get_nearest_driver_error)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState() {
        binding.btnRequestRide.isEnabled = false
        binding.progress.show()
    }

    private fun hideLoadingState() {
        binding.btnRequestRide.isEnabled = true
        binding.progress.hide()
    }
}