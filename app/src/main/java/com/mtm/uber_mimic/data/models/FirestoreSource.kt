package com.mtm.uber_mimic.data.models

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class FirestoreSource(
    @PropertyName("name")
    var name: String,

    @PropertyName("latitude")
    var latitude: Double,

    @PropertyName("longitude")
    var longitude: Double
)