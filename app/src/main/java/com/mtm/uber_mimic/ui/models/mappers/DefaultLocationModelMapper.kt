package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.ui.models.LocationModel

object DefaultLocationModelMapper : LocationModelMapper {

    override fun transform(location: Location): LocationModel {
        return LocationModel(
            id = location.id,
            name = location.name,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    override fun transform(locations: List<Location>): List<LocationModel> {
        return locations.map { transform(it) }
    }
}