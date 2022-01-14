package com.mtm.uber_mimic.tools.location

import com.google.android.gms.maps.model.LatLng
import com.mtm.uber_mimic.tools.location.exceptions.GetLocationException
import com.mtm.uber_mimic.tools.location.exceptions.LocationPermissionException

interface LocationHelper {
    @Throws(GetLocationException::class, LocationPermissionException::class)
    suspend fun getLocation(): LatLng
}