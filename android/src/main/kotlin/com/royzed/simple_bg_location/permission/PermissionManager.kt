package com.royzed.simple_bg_location.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import com.royzed.simple_bg_location.utils.findActivity
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

typealias PermissionResultCallback = (LocationPermission) -> Unit
typealias NotificationPermissionResultCallback = (Boolean) -> Unit

class PermissionManager : io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {

    private var activity: Activity? = null
    private var flutterActivityPluginBinding: ActivityPluginBinding? = null
    private var jetPackResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var notificationPermissionResultLauncher: ActivityResultLauncher<String>? = null
    private var permissionResultCallback: PermissionResultCallback? = null
    private var errorCallback: ErrorCallback? = null
    private var rationale: BackgroundPermissionRationale? = null
    private var notificationPermissionResultCallback: NotificationPermissionResultCallback? = null

    fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        flutterActivityPluginBinding = binding
        if (activity is ComponentActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jetPackRegisterResultContract()
            notificationResultContract()

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
        notificationPermissionResultLauncher = null
    }


    private fun jetPackRegisterResultContract() {
        jetPackResultLauncher = (activity!! as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handleRequestResult(permissions)
        }
    }

    private fun notificationResultContract() {
        notificationPermissionResultLauncher =
            (activity!! as ComponentActivity).registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                handleNotificationRequestResult(granted)
            }
    }
    private fun handleRequestResult(
        permissions: Map<String, @JvmSuppressWildcards Boolean>,
        requestCode: Int? = null
    ) {
        val whichModule = if (requestCode != null) "Platform " else "JetPack "
        val locationPermission: LocationPermission
        when {
            permissions.myGetOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermission =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                        || permissions.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            false
                        )
                    )
                        LocationPermission.Always
                    else
                        LocationPermission.WhileInUse
            }
            permissions.myGetOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermission =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                        || permissions.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            false
                        )
                    )
                        LocationPermission.Always
                    else
                        LocationPermission.WhileInUse
            }
            permissions.myGetOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                locationPermission = LocationPermission.Always
            }
            else -> {
                if (rationale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && permissions.size == 1
                    && permissions.keys.first() == Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) {
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        activity!!,
                        permissions.keys.first()
                    )
                    locationPermission = if (showRationale) {
                        showBackgroundPermissionRationale(activity!!, rationale!!)
                        return
                    } else {
                        LocationPermission.DeniedForever
                    }
                } else {
                    locationPermission = LocationPermission.Denied
                }
            }
        }
        rationale = null
        answerPermissionRequestResult(locationPermission)
    }

    private fun handleNotificationRequestResult(granted: Boolean) {
        if (notificationPermissionResultCallback != null) {
            notificationPermissionResultCallback!!.invoke(granted)
            notificationPermissionResultCallback = null
        }else {
            Log.e(TAG, "handleNotificationRequestResult: notificationPermissionResultCallback is null")
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {


        val permissionsString = permissions.joinToString(",", "[", "]")
        val grantsString = grantResults.joinToString(",", "[", "]") {
            it.toString()
        }


        if (requestCode == REQUEST_POST_NOTIFICATIONS_PERMISSION_CODE) {
            handleNotificationRequestResult(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return true
        }

        if (requestCode != REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE && requestCode != REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE) {
            Log.w(TAG, "request code is not mine")
            return false
        }

        //!! when upgrade to background permission but user downgrade permission,
        //!! whole app will restart, activity will became null. in this situation
        //!! just simple return false
        if (activity == null || errorCallback == null || permissionResultCallback == null) {
            Log.w(
                TAG,
                "onRequestPermissionResult activity|permissionResultCallback|errorCode == null!"
            )
            return false
        }

        if (grantResults.isEmpty()) {
            // If user interaction was interrupted, the permission request is cancelled
            // and will receive an empty array
            Log.d(TAG, "User interaction with location permission dialog was cancelled.")
            // return current permission approved
            answerPermissionRequestResult(checkPermissionStatus(activity!!))
            return true
        }
        val permissionsGrantsMap = makePermissionsGrantsMap(permissions, grantResults)

        handleRequestResult(permissionsGrantsMap, requestCode)

        return true

    }


    fun requestPermission(
        rationale: BackgroundPermissionRationale?,
        errorCallback: ErrorCallback,
        resultCallback: PermissionResultCallback
    ) {

        if (this.permissionResultCallback != null || this.errorCallback != null) {
            errorCallback(ErrorCodes.otherRequestInProgress)
            return
        }

        // Before Android M, requesting permissions was not needed.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            resultCallback(LocationPermission.Always)
            return
        }

        this.permissionResultCallback = resultCallback
        this.errorCallback = errorCallback
        this.rationale = rationale
        handleRequestPermission()

    }

    fun requestNotificationPermission(requestSinglePermissionResult: (Boolean) -> Unit) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestSinglePermissionResult(true)
            return
        } else {
            assert(notificationPermissionResultCallback == null)
            notificationPermissionResultCallback = requestSinglePermissionResult
            if (notificationPermissionResultLauncher != null) {
                notificationPermissionResultLauncher!!.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_POST_NOTIFICATIONS_PERMISSION_CODE
                )
            }
        }
    }


    private fun handleRequestPermission() {
        val alreadyHasPermission = checkPermissionStatus(activity!!)
        val requestPermissions: MutableList<String> =
            getLocationPermissionsFromManifest(activity!!.applicationContext).toMutableList()
        val bWantBackgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && bWantBackgroundPermission) {
            if (alreadyHasPermission == LocationPermission.WhileInUse) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity!!,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    if (jetPackResultLauncher != null) {
                        jetPackResultLauncher!!.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    } else {
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                        )
                    }

                } else {
                    answerPermissionRequestResult(LocationPermission.DeniedForever)
                }
            } else if (alreadyHasPermission == LocationPermission.Always) {
                // user may want change accuracy
                //requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                if (jetPackResultLauncher != null) {
                    jetPackResultLauncher!!.launch(requestPermissions.toTypedArray())
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        requestPermissions.toTypedArray(),
                        REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                    )
                }
            } else {
                // current permission is denied or deniedForever
                // Android Q allow background permission with Fine or coarse request together
                var requestCode = REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    //requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    requestCode = REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE
                } else {
                    // Android R force user to request background permission separately
                    // right now to request foreground permission, remove background permission request
                    requestPermissions.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                if (jetPackResultLauncher != null) {
                    jetPackResultLauncher!!.launch(requestPermissions.toTypedArray())
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        requestPermissions.toTypedArray(),
                        requestCode
                    )
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

    private fun answerPermissionRequestResult(
        permission: LocationPermission? = null,
        errorCode: ErrorCodes? = null
    ) {

        assert((permission != null && errorCode == null) || (permission == null && errorCode != null))

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
    private fun showBackgroundPermissionRationale(
        activity: Activity,
        rationale: BackgroundPermissionRationale
    ) {

        val permissionLabel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(
                TAG,
                "get system permissionOptionLabel ${activity.packageManager.backgroundPermissionOptionLabel}"
            )
            activity.applicationContext.packageManager.backgroundPermissionOptionLabel
        } else {
            // "VERSION.SDK_INT < R"
            "Always"
        }

        val permission = checkPermissionStatus(activity.applicationContext)
        val contentView: View
        val rationalDialog = activity.let {
            val builder = AlertDialog.Builder(it, R.style.sbgl_style_dialog_rounded_coner)
            //val builder = MaterialAlertDialogBuilder(it, R.style.sbgl_style_dialog_rounded_coner)
            builder.apply {
                val inflater = activity.layoutInflater
                contentView = inflater.inflate(R.layout.bg_permission_rationale, null)
                val titleView =
                    contentView.findViewById<TextView>(R.id.simple_bg_location_rationale_title)
                titleView.text =
                    rationale.title.ifEmpty { activity.getString(R.string.sbl_background_rationale_title) }
                val messageView =
                    contentView.findViewById<TextView>(R.id.simple_bg_location_rationale_text)
                messageView.text =
                    rationale.message.ifEmpty { activity.getString(R.string.sbl_background_rationale_text) }
                setView(contentView)
                setOnCancelListener {
                    Log.d(TAG, "Background Permission rationale dialog canceled")
                    answerPermissionRequestResult(permission)
                }
                //setCancelable(false)
            }
            builder.create()
        }

        val pButton = contentView.findViewById<Button>(R.id.btn_sbgl_positive)
        val pButtonText =
            activity.getString(R.string.sbl_background_rationale_postive_btn_text, permissionLabel)
        pButton.text = pButtonText
        val nButton = contentView.findViewById<Button>(R.id.btn_sbgl_negative)
        pButton.setOnClickListener {
            Log.d(TAG, "Background Permission Rationale Dialog user clicked PositiveButton")
            rationalDialog.dismiss()
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
        nButton.setOnClickListener {
            Log.d(TAG, "Background Permission rationale Dialog user clicked NegativeButton")
            rationalDialog.dismiss()
            answerPermissionRequestResult(permission)

        }

        rationalDialog.show()
    }


    companion object {
        private const val TAG = "SB.PermissionManager"
        private const val REQUEST_FOREGROUND_LOCATION_PERMISSION_CODE = 34
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE = 35
        private const val REQUEST_POST_NOTIFICATIONS_PERMISSION_CODE = 36

        @JvmStatic
        fun checkPermissionStatus(
            context: Context,
            bOnlyCheckBackground: Boolean = false
        ): LocationPermission {
            // If device is before Android 6.0(API 23)  , permission is always granted
            // reference https://developer.android.com/training/permissions/requesting
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return LocationPermission.Always
            }
            val permissionsInManifest = getLocationPermissionsFromManifest(context).toMutableList()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if (onlyCheckPermission == LocationPermission.WhileInUse) {
//                    permissionsInManifest.remove(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                } else if (onlyCheckPermission == LocationPermission.Always) {
//                    permissionsInManifest =
//                        mutableListOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                }
//            }

            var permission = LocationPermission.Denied
            for (permissionInManifest in permissionsInManifest) {
                val permissionStatus =
                    ContextCompat.checkSelfPermission(context, permissionInManifest)
                if (permissionInManifest == Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        permission = LocationPermission.Always
                        break
                    }
                } else {
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        permission = LocationPermission.WhileInUse
                    }
                }
            }

            if (permission == LocationPermission.Denied) {
                return permission
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return LocationPermission.Always
            } else {
                if (bOnlyCheckBackground) {
                    if (permission == LocationPermission.Always) {
                        return permission
                    }
                    if (permissionsInManifest.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val backgroundPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                        if (backgroundPermission == PackageManager.PERMISSION_GRANTED) {
                            permission = LocationPermission.Always
                        } else {
                            permission = LocationPermission.Denied
                            val activity = context.findActivity()
                            if (activity != null) {
                                val shouldShowRationale =
                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        context as Activity,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    )
                                if (!shouldShowRationale) {
                                    permission = LocationPermission.DeniedForever
                                }
                            } else {
                                Log.w(
                                    TAG, "checkPermissionStatus: for only check background"
                                            + " permission, want call shouldShowRequestPermissionRationale,"
                                            + " but activity is null. return denied as default."
                                            + " may be the context is service?"
                                )
                            }
                        }
                    } else {
                        // Only check background permission, but not in manifest. return denied forever
                        // acutally, this should throw exception
                        permission = LocationPermission.DeniedForever
                    }
                }
                return permission
            }

        }

        @JvmStatic
        fun hasPermission(context: Context): Boolean {
            val permission = checkPermissionStatus(context)
            return permission == LocationPermission.WhileInUse || permission == LocationPermission.Always
        }

        // after Android 13(API 33) , need to check notification permission
        // reference https://developer.android.com/training/permissions/requesting



        @JvmStatic
        fun hasPermissionInManifest(context: Context, permission: String): Boolean {
            return try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                        .run {
                            return this.requestedPermissions?.contains(permission) == true
                        }
                } else {
                    context.packageManager
                        .getPackageInfo(
                            context.packageName, PackageManager.PackageInfoFlags.of(
                                PackageManager.GET_PERMISSIONS.toLong()
                            )
                        )
                        .run {
                            return this.requestedPermissions?.contains(permission) == true
                        }
                }

            } catch (e: Exception) {
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
        fun getLocationPermissionsFromManifest(context: Context): List<String> {
            val hasFineLocationPermission =
                hasPermissionInManifest(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasCoarseLocationPermission =
                hasPermissionInManifest(context, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
                throw PermissionUndefinedException()
            }

            val permissions: MutableList<String> = mutableListOf()
            if (hasCoarseLocationPermission) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (hasFineLocationPermission) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (hasPermissionInManifest(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
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

            for ((index, permission) in permissions.withIndex()) {
                map[permission] = grantResults[index] == PackageManager.PERMISSION_GRANTED
            }

            return map.toMap()
        }

        @JvmStatic
        fun getAccuracyPermission(context: Context): AccuracyPermission {
            return when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                -> {
                    AccuracyPermission.Precise
                }
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                -> {
                    AccuracyPermission.Approximate
                }
                else -> {
                    AccuracyPermission.Denied
                }
            }
        }

        @JvmStatic
        fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }


}

// java Collection<T>.getOrDefault is JDK 1.8 provide. so it need Android API 24, we want support
// at least Api 21 ?
private fun Map<String, Boolean>.myGetOrDefault(key: String, default: Boolean): Boolean {

    return this[key] ?: default
}