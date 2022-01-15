package com.mtm.uber_mimic.data.destinations

import com.mtm.uber_mimic.data.destinations.mappers.FoursquareLocationMapper
import com.mtm.uber_mimic.data.destinations.models.FoursquareLocation
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.tools.location.LocationHelper
import retrofit2.http.GET
import retrofit2.http.Query

class FoursquareLocationRepository(
    private val foursquareApi: FoursquareApi,
    private val locationHelper: LocationHelper,
    private val mapper: FoursquareLocationMapper
) : LocationRepository {

    override suspend fun getLocations(): List<Location> {
        val currentLocation = locationHelper.getLocation()
        val locationString = "${currentLocation.latitude},${currentLocation.longitude}"
        val locations = foursquareApi.searchPlaces(
            latLang = locationString,
            radius = SEARCH_RADIUS
        )
        return mapper.transform(locations)
    }

    override suspend fun searchLocations(keyword: String): List<Location> {
        val currentLocation = locationHelper.getLocation()
        val locationString = "${currentLocation.latitude},${currentLocation.longitude}"
        val locations = foursquareApi.searchPlaces(
            latLang = locationString,
            radius = SEARCH_RADIUS,
            query = keyword
        )
        return mapper.transform(locations)
    }

    companion object {
        private const val SEARCH_RADIUS = 1000
    }

}

interface FoursquareApi {

    @GET("places/search")
    suspend fun searchPlaces(
        @Query("ll") latLang: String,
        @Query("radius") radius: Int,
        @Query("query") query: String? = null
    ): List<FoursquareLocation>

}