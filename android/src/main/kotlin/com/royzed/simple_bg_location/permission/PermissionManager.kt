package com.royzed.simple_bg_location.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import io.flutter.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.royzed.simple_bg_location.R
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

typealias PermissionResultCallback = (LocationPermission ) -> Unit


class PermissionManager : io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {

    private var activity: Activity? = null
    private var flutterActivityPluginBinding: ActivityPluginBinding? = null
    private var jetPackResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var permissionResultCallback: PermissionResultCallback? = null
    private var errorCallback: ErrorCallback? = null

    fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        flutterActivityPluginBinding = binding
        if (activity is ComponentActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jetPackRegisterResultContract()
        } else {
            binding.addRequestPermissionsResultListener(this)
        }
    }

//    fun onDetachedFromActivityForConfigChanges( ) {
//        onDetachedFromActivity()
//    }
//
//    fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
//        onAttachedToActivity(binding)
//    }

    fun onDetachedFromActivity() {
        if (!(activity is ComponentActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
            flutterActivityPluginBinding!!.removeRequestPermissionsResultListener(this)
        }
        this.activity = null
        flutterActivityPluginBinding = null

        jetPackResultLauncher = null
    }


    private fun jetPackRegisterResultContract() {
        jetPackResultLauncher = (activity!! as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            Log.d(TAG, "onContractsResult: $permissions")
            handleRequestResult(permissions)
        }
    }

    private fun handleRequestResult(permissions: Map<String, @JvmSuppressWildcards Boolean>, requestCode: Int? = null) {
        val whichModule = if (requestCode != null) "Platform " else "JetPack "
        val locationPermission: LocationPermission
        when {
            permissions.myGetOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d(TAG, "precise Approved in $whichModule mode")
                locationPermission =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                        || permissions.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            false
                        )
                    )
                        LocationPermission.always
                    else
                        LocationPermission.whileInUse
            }
            permissions.myGetOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d(TAG, "approximate Approved in $whichModule mode")
                locationPermission =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                        || permissions.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            false
                        )
                    )
                        LocationPermission.always
                    else
                        LocationPermission.whileInUse
            }
            permissions.myGetOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                Log.d(TAG, "background Approved in $whichModule")
                locationPermission = LocationPermission.always
            }
            else -> {
                var showRationale = false
                for (p in permissions.keys) {
                    showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity!!, p)
                    if (showRationale)
                        break
                }
                locationPermission = if (showRationale) LocationPermission.denied
                else LocationPermission.deniedForever
            }
        }
        answerPermissionRequestResult(locationPermission)
    }


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
        if (activity == null || errorCallback == null || permissionResultCallback == null) {
            Log.d(
                TAG,
                "activity != null? ${activity != null} errorCallback != null? ${errorCallback != null} resultCallBack != null? ${permissionResultCallback != null}"
            )
        }
        if (requestCode != REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE && requestCode != REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE) {
            Log.w(TAG,"request code is not mine")
            return false
        }

        //!! when upgrade to background permission but user downgrade permission,
        //!! whole app will restart, activity will became null. in this situation
        //!! just simple return false
        if (activity == null || errorCallback == null || permissionResultCallback == null) {
            Log.w(TAG,"onRequestPermissionResult activity|permissionResultCallback|errorCode == null!")
            return false
        }

        if (grantResults.isEmpty()) {
            // If user interaction was interrupted, the permission request is cancelled
            // and will receive an empty array
            Log.d(TAG,"User interaction with location permission dialog was cancelled.")
            // return current permission approved
            answerPermissionRequestResult(checkPermissionStatus(activity!!))
            return true
        }
        val permissionsGrantsMap = makePermissionsGrantsMap(permissions, grantResults)

        handleRequestResult(permissionsGrantsMap, requestCode)

        return true

    }


    fun requestPermission(
        errorCallback: ErrorCallback,
        resultCallback: PermissionResultCallback) {

        if (this.permissionResultCallback != null || this.errorCallback != null) {
            errorCallback(ErrorCodes.otherRequestInProgress)
            return
        }

        // Before Android M, requesting permissions was not needed.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resultCallback(LocationPermission.always)
            return
        }

        this.permissionResultCallback = resultCallback
        this.errorCallback = errorCallback
        handleRequestPermission()

    }



    private fun handleRequestPermission() {
        val whichModule = if (jetPackResultLauncher == null) "Platform" else "JetPack"
        Log.d(TAG,"Request permission use $whichModule")
        val alreadyHasPermission = checkPermissionStatus(activity!!.applicationContext)
        val requestPermissions: MutableList<String> =
            getLocationPermissionsFromManifest(activity!!.applicationContext).toMutableList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && hasPermissionInManifest(activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            if (alreadyHasPermission == LocationPermission.whileInUse) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showBackgroundPermissionRationale(activity!!)
                } else {
                    answerPermissionRequestResult(LocationPermission.deniedForever)
                }
            } else if (alreadyHasPermission == LocationPermission.always) {
                // user may want change accuracy
                requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                if (jetPackResultLauncher != null) {
                    jetPackResultLauncher!!.launch(requestPermissions.toTypedArray())
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        requestPermissions.toTypedArray(),
                        REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE)
                }
            } else {
                // current permission is denied or deniedForever
                // Android Q allow background permission with Fine or coarse request together
                var requestCode = REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    requestCode = REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                }
                if (jetPackResultLauncher != null) {
                    jetPackResultLauncher!!.launch(requestPermissions.toTypedArray())
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        requestPermissions.toTypedArray(),
                        requestCode)
                }
            }
        } else {
            if (jetPackResultLauncher != null) {
                jetPackResultLauncher!!.launch(requestPermissions.toTypedArray())
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    requestPermissions.toTypedArray(),
                    REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE
                )
            }
        }

    }

    private fun answerPermissionRequestResult(permission: LocationPermission? = null, errorCode: ErrorCodes? = null) {

        assert((permission != null && errorCode == null) || (permission == null && errorCode != null))
        if (permission != null) {
            Log.d(TAG,"Permission request result: $permission will send back to user.")
        } else if (errorCode != null) {
            Log.d(TAG, "Permission request failed. error code: $errorCode will send back to user.")
        }


        if (permission != null) {
            permissionResultCallback!!(permission)
        }
        if (errorCode != null) {
            errorCallback!!(errorCode)
        }

        permissionResultCallback = null
        errorCallback = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundPermissionRationale(activity: Activity) {

        val permissionLabel  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG,"get system permissionOptionLabel ${activity.packageManager.backgroundPermissionOptionLabel}")
            activity.applicationContext.packageManager.backgroundPermissionOptionLabel
        } else {
            // "VERSION.SDK_INT < R"
            "Always"
        }
        Log.d(TAG,"resultCallback: $permissionResultCallback ; errorCallback: $errorCallback")
        val permission = checkPermissionStatus(activity.applicationContext)
        val rationalDialog = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val inflater = activity.layoutInflater
                setView(inflater.inflate(R.layout.bg_permission_rationale, null))
                setPositiveButton("Change to \"$permissionLabel\"") { _, _ ->
                    Log.d(TAG, "Background Permission Rationale Dialog user clicked PositiveButton")
                    if (jetPackResultLauncher != null) {
                        jetPackResultLauncher!!.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    } else {
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                        )
                    }

                }
                setNegativeButton("No, Thanks!") { _, _ ->
                    Log.d(TAG, "Background Permission rationale Dialog user clicked NegativeButton")
                    answerPermissionRequestResult(permission)
                }
                //setTitle("Allow Title")
                //setMessage("need background location form the feature.")
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
                setOnCancelListener {
                    Log.d(TAG, "Background Permission rationale dialog canceled")
                    answerPermissionRequestResult(permission)
                }
                setCancelable(false)
            }
            builder.create()
        }
        rationalDialog.show()
    }



    companion object {
        private const val TAG = "SimpleBgl.PermissionManager"
        private const val REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE = 34
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE = 35

        @JvmStatic
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

        @JvmStatic
        fun hasPermission(context: Context): Boolean {
            val permission = checkPermissionStatus(context)
            return permission == LocationPermission.whileInUse || permission == LocationPermission.always
        }


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

//        @RequiresApi(Build.VERSION_CODES.Q)
//        private fun hasBackgroundGranted(permissions: Array<String>, grantResults: IntArray): Boolean {
//            val index = permissions.indexOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//            return index >= 0 && grantResults[index] == PackageManager.PERMISSION_GRANTED
//        }

        @JvmStatic
        private fun makePermissionsGrantsMap(permissions: Array<out String>, grantResults: IntArray)
        : Map<String, Boolean> {
            val map: MutableMap<String, Boolean> = mutableMapOf()
            assert(permissions.size == grantResults.size)

            for((index, permission) in permissions.withIndex()) {
                map[permission] = grantResults[index] == PackageManager.PERMISSION_GRANTED
            }

            return map.toMap()
        }

        @JvmStatic
        fun getAccuracyPermission(context: Context): AccuracyPermission {
            return when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                -> {
                    AccuracyPermission.precise
                }
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                -> {
                    AccuracyPermission.approximate
                }
                else -> {
                    AccuracyPermission.denied
                }
            }
        }
    }

}

// java Collection<T>.getOrDefault is JDK 1.8 provide. so it need Android API 24, we want support
// at least Api 16 ?
private fun Map<String, Boolean>.myGetOrDefault(key: String, default: Boolean): Boolean {

    return this[key]?:default
}