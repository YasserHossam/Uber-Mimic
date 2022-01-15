package com.mtm.uber_mimic.data.destinations.mappers


import com.mtm.uber_mimic.data.destinations.models.FoursquareLocation
import com.mtm.uber_mimic.domain.models.Location

interface FoursquareLocationMapper {
    fun transform(foursquareLocation: FoursquareLocation): Location

    fun transform(foursquareLocations: List<FoursquareLocation>): List<Location>
}