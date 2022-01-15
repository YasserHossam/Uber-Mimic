package com.mtm.uber_mimic.data.sources

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtm.uber_mimic.data.exceptions.GetCurrentLocationException
import com.mtm.uber_mimic.data.sources.mappers.FirestoreLocationMapper
import com.mtm.uber_mimic.data.sources.models.FirestoreLocation
import com.mtm.uber_mimic.domain.models.LatLng
import com.mtm.uber_mimic.domain.models.Location
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

class FirestoreLocationRepository(
    context: Context,
    private val mapper: FirestoreLocationMapper,
    private val schedulerProvider: SchedulerProvider
) :
    LocationRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng {
        return withContext(schedulerProvider.io()) {
            try {
                val lastLocation = Tasks.await(fusedLocationClient.lastLocation)
                return@withContext LatLng(lastLocation.latitude, lastLocation.longitude)
            } catch (throwable: Throwable) {
                Timber.e(throwable)
                throw GetCurrentLocationException()
            }
        }
    }

    override suspend fun getLocations(): List<Location> {
        val db = Firebase.firestore
        val task = db.collection(COLLECTION_NAME).limit(MAX_RESULTS_COUNT).get()
        val result = Tasks.await(task)
        val returnedList = mutableListOf<Location>()
        for (document in result.documents) {
            val source = document.toObject(FirestoreLocation::class.java)
            source?.let { returnedList.add(mapper.transform(it, document.id)) }
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
            val domainSource = source?.let { mapper.transform(it, document.id) }
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