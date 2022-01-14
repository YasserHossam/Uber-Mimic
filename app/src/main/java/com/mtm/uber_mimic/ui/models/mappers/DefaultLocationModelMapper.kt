package com.mtm.uber_mimic.ui.models.mappers

import com.mtm.uber_mimic.domain.models.Source
import com.mtm.uber_mimic.ui.models.LocationModel

object DefaultLocationModelMapper : LocationModelMapper {

    override fun transform(source: Source): LocationModel {
        return LocationModel(
            id = source.id,
            name = source.name,
            latitude = source.latitude,
            longitude = source.longitude
        )
    }

    override fun transform(sources: List<Source>): List<LocationModel> {
        return sources.map { transform(it) }
    }
}