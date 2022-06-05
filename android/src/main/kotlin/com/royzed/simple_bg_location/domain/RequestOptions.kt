package com.royzed.simple_bg_location.domain.location

data class RequestOptions(
    val accuracy: LocationAccuracy = LocationAccuracy.Best,

    /**
     *  Sets the minimum update distance between location updates. (meters)
     */
    val distanceFilter: Long = 0,

    /**
     * Sets the request interval. (Millis)
     */
    val interval: Long = 0,

    /**
     * Sets an explicit minimum update interval. (Millis)
     */
    val minUpdateInterval: Long = 0,

    /**
     * Sets the duration this request will continue before being automatically removed.
     */
    val duration: Long = 1,

    /**
     * Sets the maximum time any location update may be delayed, and thus grouped with following
     * updates to enable location batching. (Millis)
     */
    val maxUpdateDelay: Long = 0,

    /**
     * Sets the maximum number of location updates for this request before this request is
     * automatically removed.
     */
    val maxUpdates: Long = 0

)

val DEFAULT_GET_CURRENT_POSITION_LOCATION_OPTIONS = RequestOptions(
    accuracy = LocationAccuracy.High,
    distanceFilter = 0,
    interval = 3*1000,
    minUpdateInterval = 0,
    duration = 0,
    maxUpdateDelay = 0,
    maxUpdates = 1
)