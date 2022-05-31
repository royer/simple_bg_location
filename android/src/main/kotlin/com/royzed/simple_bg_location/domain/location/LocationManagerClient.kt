package com.royzed.simple_bg_location.domain.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback

class LocationManagerClient(
    val context: Context,
    private val options: LocationOptions = LocationOptions()
) : LocationClient {

    private val manager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    override fun isLocationServiceEnabled(listener: LocationServiceListener) {
        listener.onLocationServiceResult(checkLocationService(context))
    }


    override fun getLastKnownLocation(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        var bestLocation: Location? = null

        for(provider in manager.getProviders(true)) {
            @SuppressLint("MissingPermission")
            val location = manager.getLastKnownLocation(provider)
            if (location != null && isBetterLocation(location, bestLocation)) {
                bestLocation = location
            }
        }

        positionChangedCallback(bestLocation)
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

    companion object {
        private const val TAG = "LocationManagerClient"
        private const val ACCEPT_TIME_INTERVAL = 2 * 60 * 1000

        @JvmStatic
        fun isBetterLocation(location: Location, bestLocation: Location?): Boolean {
            if (bestLocation == null)
                return true

            val timeDelta = location.time - bestLocation.time
            val isSignificantlyNewer = timeDelta > ACCEPT_TIME_INTERVAL
            val isSignificantlyOlder = timeDelta < -ACCEPT_TIME_INTERVAL
            val isNewer = timeDelta > 0

            if (isSignificantlyNewer) return true
            if (isSignificantlyOlder) return false

            val accuracyDelta = (location.accuracy - bestLocation.accuracy)
            val isLessAccuracy = accuracyDelta > 0
            val isMoreAccuracy = accuracyDelta < 0
            val isSignificantlyLessAccuracy = accuracyDelta > 200

            if (isMoreAccuracy) return true
            if (isNewer && !isLessAccuracy) return true


            var isFromSameProvider = location.provider.equals(bestLocation.provider)

            if (isNewer && !isSignificantlyLessAccuracy && isFromSameProvider) return true

            return false
        }
    }
}