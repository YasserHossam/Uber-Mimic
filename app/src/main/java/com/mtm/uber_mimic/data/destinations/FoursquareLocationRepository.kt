package com.mtm.uber_mimic.data.destinations

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.mtm.uber_mimic.data.destinations.mappers.FoursquareLocationMapper
import com.mtm.uber_mimic.data.destinations.models.FoursquareResponse
import com.mtm.uber_mimic.data.exceptions.GetCurrentLocationException
import com.mtm.uber_mimic.domain.models.LatLng
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.common.scheduler.SchedulerProvider
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class FoursquareLocationRepository(
    private val foursquareApi: FoursquareApi,
    context: Context,
    private val mapper: FoursquareLocationMapper,
    private val schedulerProvider: SchedulerProvider
) : LocationRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng {
        return withContext(schedulerProvider.io()) {
            try {
                val lastLocation = Tasks.await(fusedLocationClient.lastLocation)
                return@withContext LatLng(lastLocation.latitude, lastLocation.longitude)
            } catch (throwable: Throwable) {
                throw GetCurrentLocationException()
            }
        }
    }

    override suspend fun getLocations(): List<Location> {
        val currentLocation = getCurrentLocation()
        val locationString = "${currentLocation.lat},${currentLocation.lng}"
        val response = foursquareApi.searchPlaces(
            latLang = locationString,
            radius = SEARCH_RADIUS
        )
        return mapper.transform(response.locations)
    }

    override suspend fun searchLocations(keyword: String): List<Location> {
        val currentLocation = getCurrentLocation()
        val locationString = "${currentLocation.lat},${currentLocation.lng}"
        val response = foursquareApi.searchPlaces(
            latLang = locationString,
            radius = SEARCH_RADIUS,
            query = keyword
        )
        return mapper.transform(response.locations)
    }

    companion object {
        private const val SEARCH_RADIUS = 100000
    }

}

interface FoursquareApi {

    @GET("places/search")
    suspend fun searchPlaces(
        @Query("ll") latLang: String,
        @Query("radius") radius: Int,
        @Query("query") query: String? = null
    ): FoursquareResponse

}