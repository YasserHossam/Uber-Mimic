package com.mtm.uber_mimic.data.destinations.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeoCode(
    @SerializedName("main")
    val latLng: FoursquareLatLng
)

