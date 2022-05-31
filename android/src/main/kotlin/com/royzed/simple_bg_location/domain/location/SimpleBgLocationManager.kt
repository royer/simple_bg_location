package com.royzed.simple_bg_location.domain.location

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.royzed.simple_bg_location.errors.ErrorCallback
import io.flutter.Log

class SimpleBgLocationManager {
    private lateinit var mainLocationClient: LocationClient


    companion object {
        private const val TAG = "SimpleBgLocationManager"

        @JvmStatic
        private fun createLocationClient(
            context: Context,
            forceLocationManager: Boolean = false,
            options: LocationOptions = LocationOptions()
        ) : LocationClient {

            if (forceLocationManager) {
                Log.d(TAG,"Will create Android LocationManager Client because forced")
                return LocationManagerClient(context, options ?: LocationOptions())
            }

            if (isGooglePlayServicesAvailable(context)) {
                Log.d(TAG,"Will create Google FusedLocationClient.")
                return FusedLocationClient(context, options?:LocationOptions())
            } else {
                Log.d(TAG,"Will create Android LocationManager because google play services is unavailable.")
                return LocationManagerClient(context, options ?: LocationOptions())
            }

        }

        @JvmStatic
        fun isLocationServiceEnabled(context: Context, listener: LocationServiceListener) {
            val client = createLocationClient(context, false)

            client.isLocationServiceEnabled(listener)
        }

        @JvmStatic
        fun isGooglePlayServicesAvailable(context: Context): Boolean {
            val google: GoogleApiAvailability = GoogleApiAvailability.getInstance()

            return google.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        }

        @JvmStatic
        fun getLastKnownPosition(
            context: Context,
            forceLocationManager: Boolean,
            positionChangedCallback: PositionChangedCallback,
            errorCallback: ErrorCallback) {

            val client = createLocationClient(context, forceLocationManager)
            client.getLastKnownLocation(positionChangedCallback, errorCallback)
        }

    }
}