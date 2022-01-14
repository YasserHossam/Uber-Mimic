package com.mtm.uber_mimic.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.mtm.uber_mimic.R
import com.mtm.uber_mimic.databinding.ActivityRequestRideBinding
import com.mtm.uber_mimic.ui.adapter.LocationAdapter
import com.mtm.uber_mimic.ui.closeKeyboard
import com.mtm.uber_mimic.ui.gone
import com.mtm.uber_mimic.ui.hide
import com.mtm.uber_mimic.ui.show
import com.mtm.uber_mimic.ui.viewmodel.CurrentLocationViewState
import com.mtm.uber_mimic.ui.viewmodel.LocationViewState
import com.mtm.uber_mimic.ui.viewmodel.RequestRideViewModel
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
            binding.editSource.apply {
                setText(it.name)
                setSelection(length())
                binding.editDestination.requestFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservables()
        initMap()
        initRecycler()

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

        val onEditTextClickListener: (View) -> Unit = { view ->
            binding.ivBack.show()
            binding.ivSideMenu.gone()
            if (view.id == binding.editSource.id)
                viewModel.getSources()
        }

        binding.editSource.setOnClickListener(onEditTextClickListener)
        binding.editDestination.setOnClickListener(onEditTextClickListener)

        binding.editSource.doOnTextChanged { text, _, _, _ ->
            viewModel.getSources(text.toString())
        }
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
        sourcesAdapter.submitList(locationViewState.locations)
        binding.recyclerLocation.adapter = sourcesAdapter
    }

    private fun setLocationErrorState(locationViewState: LocationViewState.Error) {
        val text = getString(R.string.get_source_error)
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState() {
        binding.progress.show()
    }

    private fun hideLoadingState() {
        binding.progress.hide()
    }
}