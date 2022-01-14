package com.mtm.uber_mimic.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.mtm.uber_mimic.R
import com.mtm.uber_mimic.databinding.ActivityRequestRideBinding
import com.mtm.uber_mimic.ui.hide
import com.mtm.uber_mimic.ui.show
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservables()
        initMap()

    }

    private fun initListeners() {
        binding.ivSideMenu.setOnClickListener {
            binding.drawerView.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_your_trips -> YourTripsActivity.start(this@RequestRideActivity)
                R.id.nav_settings -> SettingsActivity.start(this@RequestRideActivity)
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun initObservables() {
        viewModel.locationViewState.observe(this) {
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

    private fun setViewState(viewState: LocationViewState) {
        hideLoadingState()
        when (viewState) {
            is LocationViewState.Data -> setLocationDataState(viewState)
            is LocationViewState.Error -> setLocationErrorState(viewState)
            is LocationViewState.Loading -> setLoadingState()
        }
    }

    private fun setLocationDataState(viewState: LocationViewState.Data) {
        mMap.addMarker(MarkerOptions().position(viewState.latLng).title("Your Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewState.latLng, 16f))
    }

    private fun setLocationErrorState(viewState: LocationViewState.Error) {
        val errorMessage = when (viewState) {
            LocationViewState.Error.Permission -> getString(R.string.permission_error)
            LocationViewState.Error.Unknown -> getString(R.string.unknown_error)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setLoadingState() {
        binding.progress.show()
    }

    private fun hideLoadingState() {
        binding.progress.hide()
    }
}