package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Driver
import com.mtm.uber_mimic.ui.models.DriverModel

object DefaultDriverModelMapper : DriverModelMapper {

    override fun transform(location: Driver): DriverModel {
        return DriverModel(
            id = location.id,
            name = location.name,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    override fun transform(locations: List<Driver>): List<DriverModel> {
        return locations.map { transform(it) }
    }
}