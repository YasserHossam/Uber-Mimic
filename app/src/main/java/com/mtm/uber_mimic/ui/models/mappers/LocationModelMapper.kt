package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.ui.models.LocationModel

interface LocationModelMapper {
    fun transform(location: Location): LocationModel

    fun transform(locations: List<Location>): List<LocationModel>
}