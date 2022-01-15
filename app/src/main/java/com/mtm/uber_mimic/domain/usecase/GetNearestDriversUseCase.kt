package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.models.Driver

interface GetNearestDriversUseCase {
    suspend operator fun invoke(latitude: Double, longitude: Double): List<Driver>
}