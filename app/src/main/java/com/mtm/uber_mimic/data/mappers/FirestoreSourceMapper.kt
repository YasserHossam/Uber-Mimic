package com.mtm.uber_mimic.data.mappers

import com.mtm.uber_mimic.data.models.FirestoreSource
import com.mtm.uber_mimic.domain.models.Source

interface FirestoreSourceMapper {
    fun transform(firestoreSource: FirestoreSource, id: String): Source
}