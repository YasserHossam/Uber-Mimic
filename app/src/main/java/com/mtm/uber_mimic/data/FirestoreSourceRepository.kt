package com.mtm.uber_mimic.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtm.uber_mimic.data.mappers.FirestoreSourceMapper
import com.mtm.uber_mimic.data.models.FirestoreSource
import com.mtm.uber_mimic.domain.models.Source
import com.mtm.uber_mimic.domain.repo.SourceRepository

class FirestoreSourceRepository(private val firestoreSourceMapper: FirestoreSourceMapper) :
    SourceRepository {

    override suspend fun getSources(): List<Source> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(10).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Source>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreSource::class.java)
            source?.let { returnedList.add(firestoreSourceMapper.transform(it, document.id)) }
        }
        return returnedList
    }

    override suspend fun searchSources(keyword: String): List<Source> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(10).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Source>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreSource::class.java)
            source?.let { returnedList.add(firestoreSourceMapper.transform(it, document.id)) }
        }
        return returnedList
    }

    companion object {
        private const val MAX_RESULTS_COUNT = 10
        private const val COLLECTION_NAME = "Source"
    }
}