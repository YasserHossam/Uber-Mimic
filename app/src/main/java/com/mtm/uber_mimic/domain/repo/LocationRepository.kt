package com.mtm.uber_mimic.domain.repo

import com.mtm.uber_mimic.domain.models.Location

interface LocationRepository {
    suspend fun getLocations(): List<Location>

    suspend fun searchLocations(keyword: String): List<Location>
}