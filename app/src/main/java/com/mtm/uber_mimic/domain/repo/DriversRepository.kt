package com.mtm.uber_mimic.domain.repo

import com.mtm.uber_mimic.domain.models.Driver

interface DriversRepository {
    suspend fun getDrivers(): List<Driver>
}