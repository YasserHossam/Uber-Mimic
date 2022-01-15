package com.mtm.uber_mimic.data.destinations.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FoursquareLocation(
    @SerializedName("fsq_id")
    val id: String,

    @SerializedName("geocodes")
    val geoCode: GeoCode,

    @SerializedName("name")
    val name: String
)