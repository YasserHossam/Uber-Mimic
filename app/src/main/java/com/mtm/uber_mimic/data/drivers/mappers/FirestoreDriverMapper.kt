package com.mtm.uber_mimic.data.drivers.mappers

import com.mtm.uber_mimic.data.drivers.models.FirestoreDriver
import com.mtm.uber_mimic.data.sources.models.FirestoreLocation
import com.mtm.uber_mimic.domain.models.Driver
import com.mtm.uber_mimic.domain.models.Location

interface FirestoreDriverMapper {
    fun transform(firestoreDriver: FirestoreDriver, id: String): Driver
}