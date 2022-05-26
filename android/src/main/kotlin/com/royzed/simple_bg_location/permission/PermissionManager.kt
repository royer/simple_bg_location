package com.royzed.simple_bg_location.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
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
        TODO("Not yet implemented")
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
        errorCallback: ErrorCallback?,
        resultCallback: PermissionResultCallback) {

        // Before Android M, requesting permissions was not needed.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resultCallback(LocationPermission.always)
            return
        }

        val permissionsToRequest = getLocationPermissionsFromManifest(activity).toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && hasPermissionInManifest(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            val permissionStatus = checkPermissionStatus(activity)
            if (permissionStatus == LocationPermission.whileInUse) {
                permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }


        this.activity = activity;
        this.permissionResultCallback = resultCallback;
        this.errorCallback = errorCallback


        resultCallback(LocationPermission.always)
    }
    companion object {
        private const val TAG = "PermissionManager"

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
    }

}