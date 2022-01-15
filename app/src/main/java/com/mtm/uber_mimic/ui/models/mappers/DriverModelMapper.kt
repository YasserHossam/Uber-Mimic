package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Driver
import com.mtm.uber_mimic.ui.models.DriverModel

interface DriverModelMapper {
    fun transform(location: Driver): DriverModel

    fun transform(locations: List<Driver>): List<DriverModel>
}