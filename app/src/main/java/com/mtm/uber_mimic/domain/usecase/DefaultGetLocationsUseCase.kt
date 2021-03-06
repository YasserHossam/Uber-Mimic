package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.exceptions.GetLocationsException
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.common.scheduler.SchedulerProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class DefaultGetLocationsUseCase(
    private val locationRepository: LocationRepository,
    private val schedulerProvider: SchedulerProvider
) : GetLocationsUseCase {

    override suspend operator fun invoke(keyword: String): List<Location> {
        return withContext(schedulerProvider.io()) {
            try {
                return@withContext if (keyword.isEmpty())
                    locationRepository.getLocations()
                else
                    locationRepository.searchLocations(keyword)
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException)
                    throw GetLocationsException()
                else
                    return@withContext emptyList()
            }
        }
    }
}