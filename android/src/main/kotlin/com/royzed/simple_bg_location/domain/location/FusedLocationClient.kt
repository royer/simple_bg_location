package com.royzed.simple_bg_location.domain.location

import android.annotation.SuppressLint
import android.content.Context
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

    private var postionChangedCallback: PositionChangedCallback? = null
    private val errorCallback: ErrorCallback? = null

    private val fusedProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback;

    init {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult)  {
                if (postionChangedCallback == null) {
                    Log.e(TAG,"LocationCallback was called with no postionChangedCallback registered.")
                    fusedProviderClient.removeLocationUpdates(locationCallback)
                    if (errorCallback != null) {
                        errorCallback.invoke(ErrorCodes.errorWhileAcquiringPosition)
                    }
                    return
                }
                if (locationResult.locations.isNotEmpty()) {
                    for(location in locationResult.locations) {
                        postionChangedCallback!!(location)
                    }
                } else {
                    postionChangedCallback!!(locationResult.lastLocation)
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                // super.onLocationAvailability(locationAvailability)
                Log.d(TAG,"onLocationAvailability called. $locationAvailability")
                if (!locationAvailability.isLocationAvailable && !checkLocationService(context)) {
                    if (errorCallback != null) {
                        errorCallback!!(ErrorCodes.locationServicesDisabled)
                    } else {
                        Log.e(TAG,"onLocationAvailability called and no location available but errorCallback had not registered.")
                    }
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

    override fun startLoationUpdates(
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun stopLocationUpdates() {
        TODO("Not yet implemented")
    }

    override fun checkLocationService(context: Context): Boolean {
        return super.checkLocationService(context)
    }

    companion object {
        private const val TAG = "FusedLocationClient"
    }
}