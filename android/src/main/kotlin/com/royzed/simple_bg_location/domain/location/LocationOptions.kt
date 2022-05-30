package com.royzed.simple_bg_location.domain.location

data class LocationOptions(
    val accuracy: LocationAccuracy = LocationAccuracy.best,
    val distanceFilter: Long = 0,
    val timeInterval: Long = 0
)