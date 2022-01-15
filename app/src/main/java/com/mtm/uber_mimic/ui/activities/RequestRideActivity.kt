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
import com.mtm.uber_mimic.ui.adapter.LocationAdapter
import com.mtm.uber_mimic.ui.viewmodel.*
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
        viewModel.currentLocationViewState.observe(this) {
            setCurrentLocationViewState(it)
        }

        viewModel.sourcesViewState.observe(this) {
            setSourcesViewState(it)
        }

        viewModel.destinationsViewState.observe(this) {
            setSourcesViewState(it)
        }

        viewModel.driversViewState.observe(this) {
            setDriversViewState(it)
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

    private fun setCurrentLocationViewState(viewState: CurrentLocationViewState) {
        hideLoadingState()
        when (viewState) {
            is CurrentLocationViewState.Data -> setCurrentLocationDataState(viewState)
            is CurrentLocationViewState.Error -> setCurrentLocationErrorState(viewState)
            is CurrentLocationViewState.Loading -> setLoadingState()
        }
    }

    private fun setCurrentLocationDataState(viewState: CurrentLocationViewState.Data) {
        mMap.addMarker(MarkerOptions().position(viewState.latLng).title("Your Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewState.latLng, 16f))
    }

    private fun setCurrentLocationErrorState(viewState: CurrentLocationViewState.Error) {
        val errorMessage = when (viewState) {
            CurrentLocationViewState.Error.Permission -> getString(R.string.permission_error)
            CurrentLocationViewState.Error.Unknown -> getString(R.string.unknown_error)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setSourcesViewState(locationViewState: LocationViewState) {
        hideLoadingState()
        when (locationViewState) {
            is LocationViewState.Data -> setLocationDataState(locationViewState)
            is LocationViewState.Error -> setLocationErrorState(locationViewState)
            is LocationViewState.Loading -> setLoadingState()
        }
    }

    private fun setLocationDataState(locationViewState: LocationViewState.Data) {
        binding.recyclerLocation.show()
        if (locationViewState.type == LocationType.SOURCE) {
            sourcesAdapter.submitList(locationViewState.locations)
            binding.recyclerLocation.adapter = sourcesAdapter
        } else if (locationViewState.type == LocationType.DESTINATION) {
            destinationsAdapter.submitList(locationViewState.locations)
            binding.recyclerLocation.adapter = destinationsAdapter
        }
    }

    private fun setLocationErrorState(locationViewState: LocationViewState.Error) {
        val text = if (locationViewState.type == LocationType.SOURCE)
            getString(R.string.get_sources_error)
        else
            getString(R.string.get_destinations_error)
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun setDriversViewState(viewState: NearestDriversViewState) {
        hideLoadingState()
        enableRequestDriverButton()
        when (viewState) {
            is NearestDriversViewState.Data -> setDriversDataState(viewState)
            is NearestDriversViewState.Error -> setDriversErrorState(viewState)
            NearestDriversViewState.Loading -> {
                disableRequestDriverButton()
                setLoadingState()
            }
        }
    }

    private fun setDriversDataState(viewState: NearestDriversViewState.Data) {
        val drivers = viewState.drivers.map { it.toString() }
        val message = drivers.joinToString(separator = "\n\n")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setDriversErrorState(viewState: NearestDriversViewState.Error) {
        val message = if (viewState is NearestDriversViewState.Error.SourceMissing)
            getString(R.string.source_missing)
        else
            getString(R.string.unknown_driver_error)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState() {
        binding.progress.show()
    }

    private fun hideLoadingState() {
        binding.progress.hide()
    }

    private fun enableRequestDriverButton() {
        binding.btnRequestRide.isEnabled = true
    }

    private fun disableRequestDriverButton() {
        binding.btnRequestRide.isEnabled = false
    }
}