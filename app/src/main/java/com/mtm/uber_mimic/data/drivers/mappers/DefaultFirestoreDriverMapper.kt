package com.mtm.uber_mimic.data.drivers.mappers

import com.mtm.uber_mimic.data.drivers.models.FirestoreDriver
import com.mtm.uber_mimic.domain.models.Driver

object DefaultFirestoreDriverMapper : FirestoreDriverMapper {
    override fun transform(firestoreDriver: FirestoreDriver, id: String): Driver {
        return Driver(
            id = id,
            name = firestoreDriver.name,
            latitude = firestoreDriver.latitude,
            longitude = firestoreDriver.longitude
        )
    }
}