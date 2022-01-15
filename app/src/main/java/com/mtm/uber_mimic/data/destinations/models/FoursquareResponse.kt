package com.mtm.uber_mimic.data.destinations.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FoursquareResponse(
    @SerializedName("results")
    val locations: List<FoursquareLocation>
)