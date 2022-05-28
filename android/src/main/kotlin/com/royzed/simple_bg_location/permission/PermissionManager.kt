package com.royzed.simple_bg_location.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build

import io.flutter.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
import com.royzed.simple_bg_location.utils.SettingsUtils.openLocationSettings

typealias PermissionResultCallback = (LocationPermission ) -> Unit
typealias ErrorCallback = (ErrorCodes) -> Unit

class PermissionManager : io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {

    private var activity: Activity? = null
    private var permissionResultCallback: PermissionResultCallback? = null
    private var errorCallback: ErrorCallback? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        val permissionsString = permissions.joinToString(",", "[","]")
        val grantsString = grantResults.joinToString(",","[","]") {
            it.toString()
        }
        Log.d(TAG,"onRequestPermissionsResult() requestCode: $requestCode permissions: $permissionsString and grantResults: $grantsString")
        Log.d(TAG,"activity: $activity errorCallback: $errorCallback resultCallBack: $permissionResultCallback")

        //!! when upgrade to background permission but user downgrade permission,
        //!! whole app will restart, activity will became null. in this situation
        //!! just simple return false
        if (activity == null || errorCallback == null || permissionResultCallback == null) {
            Log.w(TAG,"onRequestPermissionResult activity == null!")
            return false
        }


        when(requestCode) {
            REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE,
            REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE -> when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled
                    // and will receive an empty array
                    Log.d(TAG,"User interaction was cancelled.")
                    // return current permission approved
                    answerPermissionRequestResult(checkPermissionStatus(activity!!))
                    return true
                }
                else -> {
                    val indexOfBgPermission = permissions.indexOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    if (indexOfBgPermission >= 0) {
                        if (grantResults[indexOfBgPermission] == PackageManager.PERMISSION_GRANTED) {
                            answerPermissionRequestResult(LocationPermission.always)
                            return true
                        } else {
                            val shouldShowRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            val permission =
                            if (shouldShowRationale)
                                LocationPermission.denied
                            else
                                LocationPermission.deniedForever

                            answerPermissionRequestResult(permission)
                            return true
                        }
                    } else {
                        val indexOfApproved = grantResults.indexOf(PackageManager.PERMISSION_GRANTED)
                        val permission =
                        if (indexOfApproved >= 0)
                            LocationPermission.whileInUse
                        else {
                            var shouldShowRationaleFine = false
                            for(permission in permissions) {
                                shouldShowRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(activity!!,permission )
                                Log.d(TAG, "$permission should show rationale: $shouldShowRationaleFine")
                            }
                            if (shouldShowRationaleFine)
                                LocationPermission.denied
                            else
                                LocationPermission.deniedForever
                        }

                        answerPermissionRequestResult(permission)
                        return true
                    }
                }
            }
            else -> {
                Log.w(TAG, "unknown request Permission code: $requestCode")
                return false
            }
        }





    }

    fun checkPermissionStatus(context: Context) : LocationPermission {
        // If device is before Android 6.0(API 23)  , permission is always granted
        // reference https://developer.android.com/training/permissions/requesting
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return LocationPermission.always
        }

        var permissionStatus = PackageManager.PERMISSION_DENIED

        val permissions = getLocationPermissionsFromManifest(context)
        for(permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                permissionStatus = PackageManager.PERMISSION_GRANTED
                break
            }
        }

        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            return LocationPermission.denied
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return LocationPermission.always
        }

        val wantsBackgroundLocation = hasPermissionInManifest(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (!wantsBackgroundLocation) {
            return LocationPermission.whileInUse
        }

        val permissionStatusBackground =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if(permissionStatusBackground == PackageManager.PERMISSION_GRANTED) {
            return LocationPermission.always
        }

        return LocationPermission.whileInUse
    }

    fun requestPermission(
        activity: Activity,
        errorCallback: ErrorCallback,
        resultCallback: PermissionResultCallback) {

        if (this.activity != null || this.permissionResultCallback != null || this.errorCallback != null) {
            errorCallback(ErrorCodes.otherRequestInProgress)
            return
        }

        // Before Android M, requesting permissions was not needed.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resultCallback(LocationPermission.always)
            return
        }

        this.activity = activity
        this.permissionResultCallback = resultCallback
        this.errorCallback = errorCallback

        val permissionsToRequest = getLocationPermissionsFromManifest(activity).toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && hasPermissionInManifest(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            val permissionStatus = checkPermissionStatus(activity)
            if (permissionStatus == LocationPermission.whileInUse) {
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                if (shouldShowRationale) {
                    showBackgroundPermissionRationale(activity)
                } else {
                    Log.d(TAG,"shouldShowRationale return false. return user deniedForever")
                    answerPermissionRequestResult(LocationPermission.deniedForever)
                }
            } else if(permissionStatus == LocationPermission.always) {
                answerPermissionRequestResult(LocationPermission.always)
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                    permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE)
            }
        }  else {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE)
        }

    }

    private fun answerPermissionRequestResult(permission: LocationPermission? = null, errorCode: ErrorCodes? = null) {
        Log.d(TAG,"permission: $permission, errorCode: $errorCode")
        assert((permission != null && errorCode == null) || (permission == null && errorCode != null))

        if (permission != null) {
            if (permissionResultCallback != null) {
                permissionResultCallback!!(permission)
            } else {
                Log.e(TAG,"answerPeermissionResquestResult but permissionResultCallback == null")
            }
        }
        if (errorCode != null) {
            if (errorCallback != null) {
                errorCallback!!(errorCode)
            } else {
                Log.e(TAG,"answerPermissionRequestResult but errorCallback == null")
            }
        }

        activity = null
        permissionResultCallback = null
        errorCallback = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundPermissionRationale(activity: Activity) {

        val permissionLabel  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG,"get system permissionOptionLabel ${activity.packageManager.backgroundPermissionOptionLabel}")
            activity.applicationContext.packageManager.backgroundPermissionOptionLabel
        } else {
            // TODO("VERSION.SDK_INT < R")
            "Always"
        }
        Log.d(TAG,"resultCallback: $permissionResultCallback ; errorCallback: $errorCallback")
        val permission = checkPermissionStatus(activity.applicationContext)
        val rationalDialog = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("Change to \"$permissionLabel\"") { _, _ ->
                    Log.i(TAG, "Click Allow")
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                    )

                }
                setNegativeButton("No, Thanks!") { _, _ ->
                    Log.i(TAG, "Click No Thanks")
                    if (permissionResultCallback != null) {
                        Log.d(TAG, "on No Thanks. resultCallback is not null")
                        answerPermissionRequestResult(permission)
                    } else {
                        Log.d(TAG, "on No Thanks. resultCallback is null")
                    }
                }
                setTitle("Allow Title")
                setMessage("need background location form the feature.")
//                setOnDismissListener(DialogInterface.OnDismissListener { dialog ->
//                    run {
//                        Log.i(TAG, "dismiss")
//                        if (permissionResultCallback != null) {
//                            answerPermissionRequestResult(permission)
//                        } else {
//                            Log.d(TAG, "on dismiss resultCallback is null")
//                        }
//                    }
//                })
                setOnCancelListener { dialog -> run {
                    Log.i(TAG, "Dialog canceled")
                    if (permissionResultCallback != null) {
                        answerPermissionRequestResult(permission)
                    } else {
                        Log.d(TAG, "onCancel resultCallback is null")
                    }
                } }
                setCancelable(false)
            }
            builder.create()
        }
        rationalDialog.show()
    }



    companion object {
        private const val TAG = "PermissionManager"
        private const val REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE = 34
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE = 35

        @JvmStatic
        fun hasPermissionInManifest(context: Context, permission: String) : Boolean {
            return try {
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                    .run {
                        return this.requestedPermissions?.contains(permission) == true
                }
            } catch(e: Exception) {
                Log.e(TAG, "hasPermissionInManifest() Exception: $e")
                false
            }
        }

        /**
         *  Get location permissions from manifest
         *
         *  Notice: this function only get [Manifest.permission.ACCESS_FINE_LOCATION] and
         *  [Manifest.permission.ACCESS_COARSE_LOCATION]. If want check [Manifest.permission.ACCESS_BACKGROUND_LOCATION]
         *  use [hasPermissionInManifest]
         *
         *  @param context Application context
         *
         *  @return List<String> A list of permission in manifest
         *
         *  @throws PermissionUndefinedException when neither [Manifest.permission.ACCESS_FINE_LOCATION]
         *  or [Manifest.permission.ACCESS_COARSE_LOCATION] in manifest file.
         */
        @JvmStatic
        fun getLocationPermissionsFromManifest(context: Context) : List<String> {
            val hasFineLocationPermission =
                hasPermissionInManifest(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasCoarseLocationPermission =
                hasPermissionInManifest(context, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
                throw PermissionUndefinedException()
            }

            val permissions: MutableList<String> = mutableListOf()
            if (hasFineLocationPermission) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (hasCoarseLocationPermission) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            return permissions.toList()
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun hasBackgroundGranted(permissions: Array<String>, grantResults: IntArray): Boolean {
            val index = permissions.indexOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            return index >= 0 && grantResults[index] == PackageManager.PERMISSION_GRANTED
        }
    }

}