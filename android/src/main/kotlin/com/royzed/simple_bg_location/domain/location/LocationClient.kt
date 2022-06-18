package com.royzed.simple_bg_location.domain.location

import android.content.Context
import android.location.LocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback

interface LocationClient {
    fun isLocationServiceEnabled(listener: LocationServiceListener)

    fun getLastKnownLocation(positionChangedCallback: PositionChangedCallback, errorCallback: ErrorCallback)

    fun getCurrentPosition(positionChangedCallback: PositionChangedCallback, errorCallback: ErrorCallback)

    fun startLoationUpdates(positionChangedCallback: PositionChangedCallback, errorCallback: ErrorCallback)

    fun stopLocationUpdates()

    fun checkLocationService(context: Context): Boolean {
        return isLocationServiceEnable(context)
    }
}