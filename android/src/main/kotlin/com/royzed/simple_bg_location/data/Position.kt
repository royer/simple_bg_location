package com.royzed.simple_bg_location.data

import android.location.Location
import android.os.Build
import com.royzed.simple_bg_location.utils.toIso8601String
import java.util.*

data class Position(
    val uuid: UUID = UUID.randomUUID(),
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val altitude: Double = -1.0,
    val altitudeAccuracy: Double = -1.0,
    val accuracy: Double = -1.0,
    val heading: Double = -1.0,
    val headingAccuracy: Double = -1.0,
    val floor: Int? = null,
    val speed: Double = -1.0,
    val speedAccuracy: Double = -1.0,
    val isMocked: Boolean = false
) {
    override fun toString(): String {
        val strTime = Date(timestamp).toIso8601String()
        return "$strTime - [$latitude, $longitude], altitude: $altitude"
    }

    fun toMap(): Map<String, Any> {
        val position: MutableMap<String, Any> = mutableMapOf()
        position["latitude"] = latitude
        position["uuid"] = uuid.toString()
        position["longitude"] = longitude
        position["timestamp"] = timestamp
        position["altitude"] = altitude
        position["altitudeAccuracy"] = altitudeAccuracy
        position["accuracy"] = accuracy
        position["heading"] = heading
        position["headingAccuracy"] = headingAccuracy
        if (floor != null) position["floor"] = floor
        position["speed"] = speed
        position["speedAccuracy"] = speedAccuracy
        position["isMocked"] = isMocked

        return position.toMap()
    }

    companion object {
        @JvmStatic
        fun fromLocation(location: Location): Position {
            val altitudeAccuracy: Double =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasVerticalAccuracy())
                    location.verticalAccuracyMeters.toDouble()
                else -1.0
            val headingAccuracy: Double =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasBearingAccuracy())
                    location.bearingAccuracyDegrees.toDouble()
                else -1.0
            val speedAccuracy: Double =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.hasSpeedAccuracy())
                    location.speedAccuracyMetersPerSecond.toDouble()
                else -1.0
            val isMocked =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    location.isMock
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 )
                        location.isFromMockProvider
                    else false
                }

            return Position(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = location.time,
                altitude = if (location.hasAltitude()) location.altitude else -1.0,
                altitudeAccuracy = altitudeAccuracy,
                accuracy = if (location.hasAccuracy()) location.accuracy.toDouble() else -1.0,
                heading = location.bearing.toDouble(),
                headingAccuracy = headingAccuracy,
                speed = location.speed.toDouble(),
                speedAccuracy = speedAccuracy,
                isMocked = isMocked
            )
        }
    }
}
