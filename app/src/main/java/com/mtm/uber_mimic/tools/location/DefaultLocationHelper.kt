package com.mtm.uber_mimic.tools.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.mtm.uber_mimic.tools.location.exceptions.GetLocationException
import com.mtm.uber_mimic.tools.location.exceptions.LocationPermissionException
import com.mtm.uber_mimic.tools.location.exceptions.permissionsCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class DefaultLocationHelper(private val context: Context) : LocationHelper {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): LatLng = withContext(Dispatchers.IO) {
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val isPermissionsGranted = context.permissionsCheck(requiredPermissions)
        if (isPermissionsGranted) {
            try {
                val lastLocation = Tasks.await(fusedLocationClient.lastLocation)
                return@withContext LatLng(lastLocation.latitude, lastLocation.longitude)
            } catch (throwable: Throwable) {
                Timber.e(throwable)
                throw GetLocationException()
            }
        } else
            throw LocationPermissionException()
    }
}