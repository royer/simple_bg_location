package com.royzed.simple_bg_location.domain.location

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.utils.toIso8601String
import io.flutter.Log
import java.util.*

class LocationManagerClient(
    val context: Context,
    private val options: LocationOptions = LocationOptions(),
    private val forCurrentPosition: Boolean,
) : LocationClient, LocationListener {

    private val manager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var _positionCallback: PositionChangedCallback? = null
    private var _errorCallback: ErrorCallback? = null
    private var currentProvider: String? = null
    private var isListening: Boolean = false

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

    override fun getCurrentPosition(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ) {
            getCurrentPositionApi30andHigher(positionChangedCallback, errorCallback)
        } else {
            getCurrentPositionLowerApi( positionChangedCallback, errorCallback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    private fun getCurrentPositionApi30andHigher(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback) {
        Log.d(TAG,"getCurrentPosition in Api ${Build.VERSION.SDK_INT}")
        val provider = getBestProvider(manager, LocationAccuracy.Best)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val locationRequest = LocationRequest.Builder(options.interval).apply {
                setQuality(options.accuracy.toLocationRequestQuality())
                setDurationMillis(if (options.duration >= 1) options.duration else DEFAULT_DURATION)
                setMaxUpdateDelayMillis(options.maxUpdateDelay)
                setMaxUpdateDelayMillis(options.maxUpdates)
                setMinUpdateDistanceMeters(options.distanceFilter.toFloat())
                setMinUpdateIntervalMillis(options.minUpdateInterval)
            }.build()

            manager.getCurrentLocation(provider, locationRequest, null, context.mainExecutor) {
                positionChangedCallback(it)
            }
        } else {
            manager.getCurrentLocation(provider, null, context.mainExecutor) {
                positionChangedCallback(it)
            }
        }
    }

    private fun getCurrentPositionLowerApi(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback /* = (com.royzed.simple_bg_location.errors.ErrorCodes) -> kotlin.Unit */
    ) {
        Log.d(TAG,"getCurrentPosition in low Api ${Build.VERSION.SDK_INT}")
        startLoationUpdates(positionChangedCallback, errorCallback)

    }
    @SuppressLint("MissingPermission")
    override fun startLoationUpdates(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        assert(_positionCallback == null && _errorCallback == null)

        if (!checkLocationService(context)) {
            errorCallback(ErrorCodes.locationServicesDisabled)
            return
        }

        val locationAccuracy = options.accuracy

        currentProvider = getBestProvider(manager, locationAccuracy)
        if (currentProvider == null || currentProvider!!.trim().isEmpty()) {
            errorCallback(ErrorCodes.locationServicesDisabled)
            return
        }

        _positionCallback = positionChangedCallback
        _errorCallback = errorCallback
        isListening = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val locationRequest = LocationRequest.Builder(options.interval).apply {

            }.build()
            manager.requestLocationUpdates(currentProvider!!, locationRequest, context.mainExecutor, this )
        } else {
            manager.requestLocationUpdates(
                currentProvider!!,
                options.interval, options.distanceFilter.toFloat(), this, Looper.getMainLooper())
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopLocationUpdates() {
        isListening = false
        _errorCallback = null
        _positionCallback = null
        currentProvider = null
        manager.removeUpdates(this)
    }

    @Synchronized
    override fun onLocationChanged(location: Location) {
        Log.d(TAG,"onLocationChanged(location: ${Date(location.time).toIso8601String()}, [${location.latitude}, ${location.longitude}])")
        //TODO should filter too bad location ??

        if (_positionCallback != null) {
            Log.d(TAG,"location is posted.")
            _positionCallback!!(location)
        } else {
            Log.w(TAG,"Location arrived but positionCallback is null!")
        }
        if (forCurrentPosition) {
            Log.d(TAG, "auto stop listen because this is for currentPosition call.")
            stopLocationUpdates()
        }
    }

    @TargetApi(28)
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, "onStatusChanged(provider: $provider, status: $status) be called.")
    }

    @SuppressLint("MissingPermission")
    override fun onProviderDisabled(provider: String) {
        Log.d(TAG,"onProviderDisabled($provider")
        if (provider == currentProvider) {
            if (isListening) {
                Log.d(TAG,"onProviderDisabled remove current listener.")
                manager.removeUpdates(this)
                isListening = false
            }
            if (_errorCallback != null) {
                _errorCallback!!(ErrorCodes.locationServicesDisabled)
            }

            _errorCallback = null
            _positionCallback = null
            currentProvider = null
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG,"onProviderEnabled($provider")
    }


    companion object {
        private const val TAG = "LocationManagerClient"
        private const val ACCEPT_TIME_INTERVAL:Long = 2 * 60 * 1000
        private const val DEFAULT_DURATION:Long = 30 * 1000

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


            val isFromSameProvider = location.provider.equals(bestLocation.provider)

            if (isNewer && !isSignificantlyLessAccuracy && isFromSameProvider) return true

            return false
        }

        @JvmStatic
        fun getBestProvider(locationManager: LocationManager, accuracy: LocationAccuracy): String {

            val criteria = Criteria()
            criteria.isBearingRequired =false
            criteria.isAltitudeRequired = false
            criteria.isSpeedRequired = false

            when(accuracy) {
                LocationAccuracy.Lowest -> {
                    criteria.accuracy = Criteria.NO_REQUIREMENT
                    criteria.horizontalAccuracy = Criteria.NO_REQUIREMENT
                    criteria.powerRequirement = Criteria.NO_REQUIREMENT
                }
                LocationAccuracy.Low -> {
                    criteria.accuracy = Criteria.ACCURACY_COARSE
                    criteria.horizontalAccuracy = Criteria.ACCURACY_LOW
                    criteria.powerRequirement = Criteria.POWER_LOW
                }
                LocationAccuracy.Medium -> {
                    criteria.accuracy = Criteria.ACCURACY_FINE
                    criteria.horizontalAccuracy = Criteria.ACCURACY_MEDIUM
                    criteria.powerRequirement = Criteria.POWER_MEDIUM
                }
                else -> {
                    criteria.accuracy = Criteria.ACCURACY_FINE
                    criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
                    criteria.powerRequirement = Criteria.POWER_HIGH
                }
            }

            var provider = locationManager.getBestProvider(criteria, true)
            if (provider == null || provider.trim().isEmpty()) {
                val providers: List<String> = locationManager.getProviders(true)
                if (providers.isNotEmpty()) provider = providers[0].trim()
            }
            return provider!!
        }
    }

}