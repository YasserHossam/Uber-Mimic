package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.models.LatLng

interface GetCurrentLocationUseCase {
    suspend operator fun invoke(): LatLng
}