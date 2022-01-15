package com.mtm.uber_mimic.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtm.uber_mimic.data.mappers.FirestoreLocationMapper
import com.mtm.uber_mimic.data.models.FirestoreLocation
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository

class FirestoreLocationRepository(private val firestoreLocationMapper: FirestoreLocationMapper) :
    LocationRepository {

    override suspend fun getLocations(): List<Location> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(MAX_RESULTS_COUNT).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Location>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreLocation::class.java)
            source?.let { returnedList.add(firestoreLocationMapper.transform(it, document.id)) }
        }
        return returnedList
    }

    override suspend fun searchLocations(keyword: String): List<Location> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(MAX_RESULTS_COUNT).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Location>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreLocation::class.java)
            val domainSource = source?.let { firestoreLocationMapper.transform(it, document.id) }
            domainSource?.let {
                if (it.name.lowercase().contains(keyword.lowercase()))
                    returnedList.add(it)
            }
        }
        return returnedList
    }

    companion object {
        private const val MAX_RESULTS_COUNT = 10L
        private const val COLLECTION_NAME = "Source"
    }
}