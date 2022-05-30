package com.royzed.simple_bg_location.domain.location

import android.content.Context
import android.location.LocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback

class LocationManagerClient(
    val context: Context,
    private val options: LocationOptions = LocationOptions()
) : LocationClient {

    private val manager: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    override fun isLocationServiceEnabled(listener: LocationServiceListener) {
        if (manager == null) {
            listener.onLocationServiceResult(false)
            return
        }
        listener.onLocationServiceResult(checkLocationService(context))
    }

    override fun getLastKnownLocation(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun startLoationUpdates(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun stopLocationUpdates() {
        TODO("Not yet implemented")
    }
}