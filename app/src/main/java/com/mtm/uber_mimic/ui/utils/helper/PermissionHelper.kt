package com.mtm.uber_mimic.ui.utils.helper

import android.Manifest
import android.content.Context
import com.mtm.uber_mimic.tools.location.exceptions.permissionsCheck

class PermissionHelper(private val context: Context) {
    suspend fun isLocationPermissionsGranted(): Boolean {
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return context.permissionsCheck(requiredPermissions)
    }
}