package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Source
import com.mtm.uber_mimic.ui.models.LocationModel

interface LocationModelMapper {
    fun transform(source: Source): LocationModel

    fun transform(sources: List<Source>): List<LocationModel>
}