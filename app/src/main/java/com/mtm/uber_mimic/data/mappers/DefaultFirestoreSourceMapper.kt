package com.mtm.uber_mimic.data.mappers

import com.mtm.uber_mimic.data.models.FirestoreSource
import com.mtm.uber_mimic.domain.models.Source

object DefaultFirestoreSourceMapper : FirestoreSourceMapper {
    override fun transform(firestoreSource: FirestoreSource, id: String): Source {
        return Source(
            id = id,
            name = firestoreSource.name,
            latitude = firestoreSource.latitude,
            longitude = firestoreSource.longitude
        )
    }
}