package com.mtm.uber_mimic.data.sources.mappers

import com.mtm.uber_mimic.data.sources.models.FirestoreLocation
import com.mtm.uber_mimic.domain.models.Location

object DefaultFirestoreLocationMapper : FirestoreLocationMapper {
    override fun transform(firestoreLocation: FirestoreLocation, id: String): Location {
        return Location(
            id = id,
            name = firestoreLocation.name,
            latitude = firestoreLocation.latitude,
            longitude = firestoreLocation.longitude
        )
    }
}