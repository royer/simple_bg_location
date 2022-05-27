package com.royzed.simple_bg_location.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.royzed.simple_bg_location.R
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
typealias PermissionResultCallback = (LocationPermission ) -> Unit
typealias ErrorCallback = (ErrorCodes) -> Unit

class PermissionManager : io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {

    private var activity: Activity? = null
    private var permissionResultCallback: PermissionResultCallback? = null
    private var errorCallback: ErrorCallback? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            Log.d(TAG,"receive requestCode($requestCode) != my request code($PERMISSION_REQUEST_CODE)")
            return false
        }

        if (activity == null) {
            if (errorCallback != null) {
                errorCallback!!(ErrorCodes.activityMissing)
            }
            return false
        }

        if (grantResults.size == 0) {
            Log.i(TAG,"The grantResults array is empty. This can happen the use cancels the permission request")
            //TODO should we error to user?
            errorCallback?:(ErrorCodes.activityMissing)
            return false
        }

        var requestedPermissions : List<String>? = null;
        try {
            requestedPermissions = getLocationPermissionsFromManifest(activity!!)
        } catch (ex: PermissionUndefinedException) {
            errorCallback?:(ErrorCodes.permissionDefinitionsNotFound)
            return false
        }

        var locationPermission: LocationPermission = LocationPermission.denied
        var grantedResult = PackageManager.PERMISSION_DENIED
        var permissionPartOfPermissionsResult = false
        var shouldShowRationale = false

        for(permission in requestedPermissions) {
            val index = permissions.indexOf(permission)
            if (index >= 0) {
                permissionPartOfPermissionsResult = true
            }
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                grantedResult = PackageManager.PERMISSION_GRANTED
            }
            if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                shouldShowRationale = true
            }
        }

        if (!permissionPartOfPermissionsResult) {
            Log.w(TAG, "Location permissions not part of permissions send to onRequestPermissionsRequest methon")
            return false
        }

        if (grantedResult == PackageManager.PERMISSION_GRANTED) {
            locationPermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || hasBackgroundGranted(permissions as Array<String>, grantResults)) {
                LocationPermission.always
            } else {

                LocationPermission.whileInUse
            }
        } else {
            if (!shouldShowRationale) {
                locationPermission = LocationPermission.deniedForever
            }
        }
        permissionResultCallback!!(locationPermission)
        cleanPermissionCallback()
        return true

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

        // Before Android M, requesting permissions was not needed.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resultCallback(LocationPermission.always)
            return
        }

        this.activity = activity;
        this.permissionResultCallback = resultCallback;
        this.errorCallback = errorCallback



        val permissionsToRequest = getLocationPermissionsFromManifest(activity).toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && hasPermissionInManifest(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            val permissionStatus = checkPermissionStatus(activity)
            if (permissionStatus == LocationPermission.whileInUse) {
                // permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                if (shouldShowRationale) {
                    showBackgroundPermissionRationale(activity)
                }
            } else {
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }  else {
            resultCallback(LocationPermission.always)
        }




    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
                setPositiveButton("Change to \"$permissionLabel\"", DialogInterface.OnClickListener { dialog, id ->
                    Log.i(TAG, "Click Allow")
                    errorCallback!!(ErrorCodes.permissionDenied)
                    cleanPermissionCallback()
                })
                setNegativeButton("No, Thanks!", DialogInterface.OnClickListener { dialog, id ->
                    Log.i(TAG, "Click No Thanks")
                    if (permissionResultCallback != null) {
                        Log.d(TAG,"on No Thanks. resultCallback is not null")
                        permissionResultCallback!!(permission)
                        cleanPermissionCallback()
                    } else {
                        Log.d(TAG,"on No Thanks. resultCallback is null")
                    }
                })
                setTitle("Allow Title")
                setMessage("need background location form the feature.")
                setOnDismissListener(DialogInterface.OnDismissListener { dialog ->
                    run {
                        Log.i(TAG, "dismiss")
                        if (permissionResultCallback != null) {
                            permissionResultCallback!!(permission)
                            cleanPermissionCallback()
                        } else {
                            Log.d(TAG, "on dismiss resultCallback is null")
                        }
                    }
                })
                setOnCancelListener { dialog -> run {
                    Log.i(TAG, "Dialog canceled")
                    if (permissionResultCallback != null) {
                        permissionResultCallback!!(permission)
                        cleanPermissionCallback()
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

    private fun cleanPermissionCallback() {
        activity = null
        permissionResultCallback = null
        errorCallback = null
    }

    companion object {
        private const val TAG = "PermissionManager"
        private const val PERMISSION_REQUEST_CODE = 34

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

        private fun hasBackgroundGranted(permissions: Array<String>, grantResults: IntArray): Boolean {
            val index = permissions.indexOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            return index >= 0 && grantResults[index] == PackageManager.PERMISSION_GRANTED
        }
    }

}