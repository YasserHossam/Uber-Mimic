package com.mtm.uber_mimic.data.destinations.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeoCode(
    @SerializedName("main")
    val latLng: FoursquareLatLng
)

@Keep
data class FoursquareLatLng(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)