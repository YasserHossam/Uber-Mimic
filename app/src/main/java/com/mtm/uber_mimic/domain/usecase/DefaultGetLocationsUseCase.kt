package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.exceptions.GetLocationsException
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                Timber.e(throwable)
                throw GetLocationsException()
            }
        }
    }
}