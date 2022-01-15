package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.models.Location

interface GetLocationsUseCase {
    suspend operator fun invoke(keyword: String = ""): List<Location>
}