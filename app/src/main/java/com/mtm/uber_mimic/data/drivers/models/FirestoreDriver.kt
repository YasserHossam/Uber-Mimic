package com.mtm.uber_mimic.data.drivers.models

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
class FirestoreDriver {
    @PropertyName("name")
    var name: String = ""

    @PropertyName("latitude")
    var latitude: Double = 0.0

    @PropertyName("longitude")
    var longitude: Double = 0.0
}
