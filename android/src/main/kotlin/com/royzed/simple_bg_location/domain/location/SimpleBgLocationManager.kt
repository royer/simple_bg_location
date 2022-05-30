package com.royzed.simple_bg_location.domain.location

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.flutter.Log

class SimpleBgLocationManager {
    private lateinit var mainLocationClient: LocationClient

    fun isLocationServiceEnabled(context: Context, listener: LocationServiceListener) {
        val client = createLocationClient(context, false)

        client.isLocationServiceEnabled(listener)
    }

    private fun createLocationClient(
        context: Context,
        forceAndroidLocationManager: Boolean = false,
        options: LocationOptions = LocationOptions()
    ) : LocationClient {

        if (forceAndroidLocationManager) {
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

    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val google: GoogleApiAvailability = GoogleApiAvailability.getInstance()

        return google.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    companion object {
        private const val TAG = "SimpleBgLocationManager"
    }
}