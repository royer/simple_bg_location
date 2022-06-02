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
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps_enabled || network_enabled
    }
}