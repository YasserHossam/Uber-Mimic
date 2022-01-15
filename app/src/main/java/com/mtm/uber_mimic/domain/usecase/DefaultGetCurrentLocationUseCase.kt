package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.models.LatLng
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import kotlinx.coroutines.withContext

class DefaultGetCurrentLocationUseCase(
    private val locationsRepository: LocationRepository,
    private val schedulerProvider: SchedulerProvider
) : GetCurrentLocationUseCase {

    override suspend fun invoke(): LatLng {
        return withContext(schedulerProvider.io()) {
            return@withContext locationsRepository.getCurrentLocation()
        }
    }
}