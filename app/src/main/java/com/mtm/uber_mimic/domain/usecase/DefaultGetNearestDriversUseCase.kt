package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.exceptions.GetNearestDriversException
import com.mtm.uber_mimic.domain.models.Driver
import com.mtm.uber_mimic.domain.repo.DriversRepository
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.sqrt

class DefaultGetNearestDriversUseCase(
    private val driversRepository: DriversRepository,
    private val schedulerProvider: SchedulerProvider
) : GetNearestDriversUseCase {

    override suspend fun invoke(latitude: Double, longitude: Double): List<Driver> {
        return withContext(schedulerProvider.io()) {
            val drivers = try {
                driversRepository.getDrivers()
            } catch (throwable: Throwable) {
                Timber.e(throwable)
                if (throwable !is CancellationException)
                    throw GetNearestDriversException()
                throw throwable
            }

            val euclideanWithIdList = mutableListOf<EuclideanWithId>()
            for (driver in drivers) {
                val xDiff = (driver.latitude - latitude).pow(2)
                val yDiff = (driver.longitude - longitude).pow(2)
                val distance = sqrt(xDiff + yDiff)
                euclideanWithIdList.add(EuclideanWithId(distance, driver.id))
            }
            return@withContext euclideanWithIdList
                .sortedBy { it.distance }
                .take(4)
                .mapNotNull { drivers.find { driver -> driver.id == it.driverId } }
        }
    }

    data class EuclideanWithId(val distance: Double, val driverId: String)
}