package com.royzed.simple_bg_location.domain

import android.content.Context
import com.royzed.simple_bg_location.domain.location.LocationAccuracy
import com.royzed.simple_bg_location.utils.parseFlutterIntToLong
import io.flutter.Log

data class RequestOptions(
    val accuracy: LocationAccuracy = LocationAccuracy.Best,

    /**
     *  Sets the minimum update distance between location updates. (meters)
     */
    val distanceFilter: Long = 0,

    val forceLocationManager: Boolean = false,

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
    val duration: Long = 0,

    /**
     * Sets the maximum time any location update may be delayed, and thus grouped with following
     * updates to enable location batching. (Millis)
     */
    val maxUpdateDelay: Long = 0,

    /**
     * Sets the maximum number of location updates for this request before this request is
     * automatically removed.
     */
    val maxUpdates: Long = 0,

    val notificationConfig: ForegroundNotificationConfig = ForegroundNotificationConfig()

) {

    fun toMap(): Map<String, Any>? {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["accuracy"] = accuracy.ordinal
        map["distanceFilter"] = distanceFilter
        map["forceLocationManager"] = forceLocationManager
        map["interval"] = interval
        map["minUpdateInterval"] = minUpdateInterval
        map["duration"] = duration
        map["maxUpdateDelay"] = maxUpdateDelay
        map["maxUpdates"] = maxUpdates
        map["notificationConfig"] = notificationConfig.toMap()

        return map.toMap()
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun fromMap(context: Context, map: Map<String, Any?>?): RequestOptions {
            if (map == null) {
                return makeDefault(context)
            }


            return RequestOptions(
                accuracy = LocationAccuracy.fromInt((map["accuracy"] as? Int)?:0),
                distanceFilter = parseFlutterIntToLong(map["distanceFilter"]) ?: 0,
                forceLocationManager = (map["forceLocationManager"] as? Boolean) == true,
                interval = parseFlutterIntToLong(map["interval"]) ?: 0,
                minUpdateInterval = parseFlutterIntToLong(map["minUpdateInterval"]) ?: 0,
                duration = parseFlutterIntToLong(map["duration"]) ?: 0,
                maxUpdateDelay = parseFlutterIntToLong(map["maxUpdateDelay"]) ?: 0,
                maxUpdates = parseFlutterIntToLong(map["maxUpdates"]) ?: 0,
                notificationConfig = ForegroundNotificationConfig.fromMap(context, map["notificationConfig"] as Map<String, Any?>?)
            )
        }

        @JvmStatic
        fun makeDefault(context: Context): RequestOptions {
            return RequestOptions(
                accuracy = LocationAccuracy.Medium,
                distanceFilter = 0,
                forceLocationManager = false,
                interval = 0,
                minUpdateInterval = 0,
                duration = 0,
                maxUpdateDelay = 0,
                maxUpdates = 0,
                notificationConfig = ForegroundNotificationConfig.makeDefaultConfig(context)
            )
        }
    }
}

val DEFAULT_GET_CURRENT_POSITION_LOCATION_OPTIONS = RequestOptions(
    accuracy = LocationAccuracy.High,
    distanceFilter = 0,
    interval = 3*1000,
    minUpdateInterval = 0,
    duration = 0,
    maxUpdateDelay = 0,
    maxUpdates = 1
)