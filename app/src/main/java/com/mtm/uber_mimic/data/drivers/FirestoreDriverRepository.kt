package com.mtm.uber_mimic.data.drivers

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtm.uber_mimic.data.drivers.mappers.FirestoreDriverMapper
import com.mtm.uber_mimic.data.drivers.models.FirestoreDriver
import com.mtm.uber_mimic.domain.models.Driver
import com.mtm.uber_mimic.domain.repo.DriversRepository

class FirestoreDriverRepository(private val mapper: FirestoreDriverMapper) :
    DriversRepository {

    override suspend fun getDrivers(): List<Driver> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(MAX_RESULTS_COUNT).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Driver>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreDriver::class.java)
            source?.let { returnedList.add(mapper.transform(it, document.id)) }
        }
        return returnedList
    }

    companion object {
        private const val MAX_RESULTS_COUNT = 10L
        private const val COLLECTION_NAME = "Driver"
    }
}