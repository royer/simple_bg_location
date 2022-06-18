package com.royzed.simple_bg_location.domain.location

import android.content.Context
import android.location.LocationManager

fun isLocationServiceEnable(context: Context): Boolean {
    val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gps_enabled || network_enabled
}