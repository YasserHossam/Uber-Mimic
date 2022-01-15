package com.mtm.uber_mimic.data.mappers

import com.mtm.uber_mimic.data.models.FirestoreLocation
import com.mtm.uber_mimic.domain.models.Location

interface FirestoreLocationMapper {
    fun transform(firestoreLocation: FirestoreLocation, id: String): Location
}