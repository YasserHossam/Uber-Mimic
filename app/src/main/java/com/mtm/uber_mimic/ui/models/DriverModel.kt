package com.mtm.uber_mimic.ui.models

data class DriverModel(
    val id: String,
    val name: String,
    val longitude: Double,
    val latitude: Double
) {
    override fun toString(): String {
        return "$name     ${latitude},${longitude}"
    }
}