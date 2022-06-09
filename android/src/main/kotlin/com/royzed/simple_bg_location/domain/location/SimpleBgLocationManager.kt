package com.royzed.simple_bg_location.domain.location

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.royzed.simple_bg_location.domain.DEFAULT_GET_CURRENT_POSITION_LOCATION_OPTIONS
import com.royzed.simple_bg_location.domain.RequestOptions
import com.royzed.simple_bg_location.errors.ErrorCallback
import io.flutter.Log

class SimpleBgLocationManager(
    private val context: Context
) {
    private var mainLocationClient: LocationClient? = null

    fun getCurrentPosition(
        forceLocationManager: Boolean,
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        val locationOptions = DEFAULT_GET_CURRENT_POSITION_LOCATION_OPTIONS

        val client = createLocationClient(
            context,
            forceLocationManager,
            locationOptions,
            true)
        client.getCurrentPosition(positionChangedCallback, errorCallback)
    }

    fun startPositionUpdate(
        options: RequestOptions,
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback ) {

        assert(mainLocationClient == null)

        mainLocationClient = createLocationClient(context, options.forceLocationManager, options, false);
        mainLocationClient!!.startLoationUpdates(positionChangedCallback, errorCallback);
    }

    fun stopPosition() {

        mainLocationClient!!.stopLocationUpdates()
        mainLocationClient = null
    }

    companion object {
        private const val TAG = "SimpleBgLocationManager"

        @JvmStatic
        private fun createLocationClient(
            context: Context,
            forceLocationManager: Boolean = false,
            options: RequestOptions = RequestOptions(),
            forCurrentPosition: Boolean
        ) : LocationClient {

            if (forceLocationManager) {
                Log.d(TAG,"Will create Android LocationManager Client because forced")
                return LocationManagerClient(context, options ?: RequestOptions(), forCurrentPosition)
            }

            if (isGooglePlayServicesAvailable(context)) {
                Log.d(TAG,"Will create Google FusedLocationClient.")
                return FusedLocationClient(context, options?:RequestOptions())
            } else {
                Log.d(TAG,"Will create Android LocationManager because google play services is unavailable.")
                return LocationManagerClient(context, options ?: RequestOptions(), forCurrentPosition)
            }

        }

        @JvmStatic
        fun isLocationServiceEnabled(context: Context, listener: LocationServiceListener) {
            val client = createLocationClient(context, false, RequestOptions(), false)

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

            val client = createLocationClient(context, forceLocationManager, DEFAULT_GET_CURRENT_POSITION_LOCATION_OPTIONS,true)
            client.getLastKnownLocation(positionChangedCallback, errorCallback)
        }

    }
}