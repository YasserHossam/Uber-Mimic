package com.mtm.uber_mimic.domain.repo

import com.mtm.uber_mimic.data.exceptions.GetCurrentLocationException
import com.mtm.uber_mimic.domain.models.LatLng
import com.mtm.uber_mimic.domain.models.Location

interface LocationRepository {

    @Throws(GetCurrentLocationException::class)
    suspend fun getCurrentLocation(): LatLng

    suspend fun getLocations(): List<Location>

    suspend fun searchLocations(keyword: String): List<Location>
}