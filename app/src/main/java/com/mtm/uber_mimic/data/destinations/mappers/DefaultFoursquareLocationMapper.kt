package com.mtm.uber_mimic.data.destinations.mappers


import com.mtm.uber_mimic.data.destinations.models.FoursquareLocation
import com.mtm.uber_mimic.domain.models.Location

object DefaultFoursquareLocationMapper : FoursquareLocationMapper {

    override fun transform(foursquareLocation: FoursquareLocation): Location {
        return Location(
            id = foursquareLocation.id,
            name = foursquareLocation.name,
            latitude = foursquareLocation.geoCode.latLng.latitude,
            longitude = foursquareLocation.geoCode.latLng.longitude
        )
    }

    override fun transform(foursquareLocations: List<FoursquareLocation>): List<Location> {
        return foursquareLocations.map { transform(it) }
    }
}