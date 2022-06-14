package com.royzed.simple_bg_location.domain.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.royzed.simple_bg_location.domain.RequestOptions
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.errors.ErrorCodes
import io.flutter.Log

class FusedLocationClient(
    private val context: Context,
    private val options: RequestOptions = RequestOptions()
): LocationClient {

    private var _postionChangedCallback: PositionChangedCallback? = null
    private var _errorCallback: ErrorCallback? = null

    private val fusedProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback;



    init {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult)  {
                if (_postionChangedCallback == null) {
                    Log.e(TAG,"LocationCallback was called with no postionChangedCallback registered.")
                    fusedProviderClient.removeLocationUpdates(locationCallback)
                    if (_errorCallback != null) {
                        _errorCallback!!(ErrorCodes.errorWhileAcquiringPosition)
                    }
                    return
                }
                if (locationResult.locations.isNotEmpty()) {

                    if (locationResult.locations.size > 1) {
                        Log.d(TAG,"batch location update arrived: size: ${locationResult.locations.size}")
                    }
                    for(location in locationResult.locations) {
                        _postionChangedCallback!!(location)
                    }
                } else {
                    _postionChangedCallback!!(locationResult.lastLocation)
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                // super.onLocationAvailability(locationAvailability)

                if (!locationAvailability.isLocationAvailable && !checkLocationService(context)) {
                    if (_errorCallback != null) {
                        _errorCallback!!(ErrorCodes.locationServicesDisabled)
                    } else {
                        Log.e(TAG,"onLocationAvailability called and no location available but errorCallback had not registered.")
                    }
                    stopLocationUpdates()
                }
            }
        }

    }


    override fun isLocationServiceEnabled(listener: LocationServiceListener) {
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(LocationSettingsRequest.Builder().build())
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    val result = it.result
                    val settingsStates = result.locationSettingsStates
                    listener.onLocationServiceResult(
                        settingsStates?.isGpsUsable ?: false
                                || settingsStates?.isNetworkLocationUsable ?: false
                    )
                } else {
                    listener.onLocationServiceError(ErrorCodes.locationServicesDisabled)
                }
            }
            .addOnFailureListener {
                //todo should send to exception report?
                Log.e(TAG,"isLocationServiceEnabled failed. $it")
                listener.onLocationServiceError(ErrorCodes.locationServicesDisabled)
            }
    }

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        fusedProviderClient.lastLocation.addOnSuccessListener(positionChangedCallback)
            .addOnFailureListener {
                Log.e(TAG,"Error trying to get last known location. $it")
                errorCallback(ErrorCodes.errorWhileAcquiringPosition)
            }
    }

    @SuppressLint("MissingPermission")
    override fun getCurrentPosition(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        val cts:CancellationTokenSource = CancellationTokenSource()
        fusedProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener {
                positionChangedCallback(it)
                cts.cancel()

            }
            .addOnFailureListener {
                Log.e(TAG,"failed to try getCurrentPostion. $it")
                errorCallback(ErrorCodes.errorWhileAcquiringPosition)
                cts.cancel()
            }
            .addOnCanceledListener {
                Log.e(TAG,"getCurrentPosition cancelled.")
                errorCallback(ErrorCodes.errorWhileAcquiringPosition)
                cts.cancel()
            }
        //cts.cancel()
    }

    @SuppressLint("MissingPermission")
    override fun startLoationUpdates(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        assert(_postionChangedCallback == null && _errorCallback == null)

        if (!checkLocationService(context)) {
            errorCallback(ErrorCodes.locationServicesDisabled)
            return
        }

        val locationRequest: LocationRequest = LocationRequest.create().apply {
            priority = options.accuracy.toGoogleLocationRequestQuality()
            if (priority == com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY) {
                isWaitForAccurateLocation = true
            }
            if (options.interval>0) {
                interval = options.interval
            }else {
                interval = 1
            }
            if (options.distanceFilter > 0) {
                smallestDisplacement = options.distanceFilter.toFloat()
            }
            if (options.minUpdateInterval > 0) {
                fastestInterval = options.minUpdateInterval
            }
            if (options.duration > 0) {
                setExpirationDuration(options.duration)
            }
            if (options.maxUpdateDelay > 0) {
                maxWaitTime = options.maxUpdateDelay
            }
            if (options.maxUpdates > 0) {
                numUpdates = options.maxUpdates.toInt()
            }
        }

        try {
            _postionChangedCallback = positionChangedCallback
            _errorCallback = errorCallback
            fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        } catch (e: SecurityException) {
            _errorCallback!!(ErrorCodes.permissionDenied)
            _errorCallback = null
            _postionChangedCallback = null
        }

    }

    override fun stopLocationUpdates() {
        fusedProviderClient.removeLocationUpdates(locationCallback)
        _postionChangedCallback = null
        _errorCallback = null
    }

    override fun checkLocationService(context: Context): Boolean {
        return super.checkLocationService(context)
    }

    companion object {
        private const val TAG = "FusedLocationClient"
    }
}