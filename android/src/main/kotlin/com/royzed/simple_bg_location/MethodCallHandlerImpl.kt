package com.royzed.simple_bg_location

import android.app.Activity
import android.content.Context
import android.util.Log
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.domain.location.LocationServiceListener
import com.royzed.simple_bg_location.domain.location.SimpleBgLocationManager
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
import com.royzed.simple_bg_location.permission.PermissionManager
import com.royzed.simple_bg_location.utils.SettingsUtils
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private const val TAG = "MethodCallHandlerImpl"

class MethodCallHandlerImpl(
    private val permissionManager: PermissionManager
) : MethodChannel.MethodCallHandler {



    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var _channel : MethodChannel? = null
    private val channel : MethodChannel get() = _channel!!

    private var _context: Context? = null
    private val context get() = _context!!

    private var _activity: Activity? = null
    fun setActivity(activity: Activity) {
        _activity = activity
    }

    private val activity get() = _activity!!




    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            Methods.checkPermission -> {
                onCheckPermission(result)
            }
            Methods.requestPermission -> {
                onRequestPermission(result)
            }
            Methods.isLocationServiceEnabled -> {
                onIsLocationServiceEnable(result)
            }
            Methods.getAccuracyPermission -> {
                onGetAccuracyPermission(result)
            }
            Methods.getLastKnownPosition -> {
                onGetLastKnownPosition(call, result)
            }
            Methods.openAppSettings -> {
                val hasOpenAppSettings = SettingsUtils.openAppSettings(context)
                result.success(hasOpenAppSettings)
            }
            Methods.openLocationSettings -> {
                val hasOpenLocationSettings = SettingsUtils.openLocationSettings(context)
                result.success(hasOpenLocationSettings)
            }
            else -> {
                result.notImplemented()
            }
        }

    }


    fun startListening(context: Context, messenger: BinaryMessenger) {
        if (_channel != null) {
            Log.w(TAG, "Setting a method call handler before the last was disposed.")
            stopListening()
        }
        _channel = MethodChannel(messenger, "com.royzed.simple_bg_location/methods")
        channel.setMethodCallHandler(this)
        this._context = context
    }

    fun stopListening() {
        if (_channel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had benn initialized.")
            return
        }

        channel.setMethodCallHandler(null)
        _channel = null
    }

    private fun onCheckPermission(result: MethodChannel.Result) {
        try {
            val permission = PermissionManager.checkPermissionStatus(context)
            result.success(permission.ordinal)

        } catch (e: PermissionUndefinedException) {
            Log.e(TAG,"onCheckPermission() got Exception: $e")
            val errorCode = ErrorCodes.permissionDefinitionsNotFound
            result.error(errorCode.code, errorCode.description, null)
        }
    }

    private fun onRequestPermission(result: MethodChannel.Result) {
        try {
            permissionManager.requestPermission( {
                result.error(it.code, it.description, null)
            }) {
                result.success(it.ordinal)
            }
            Log.d(TAG,"onRequestPermission finished")
        } catch (e: PermissionUndefinedException) {
            val errorCode = ErrorCodes.permissionDefinitionsNotFound
            result.error(errorCode.code, errorCode.description, null)
        }
    }

    private fun onIsLocationServiceEnable(result: MethodChannel.Result) {
        try {
            SimpleBgLocationManager.isLocationServiceEnabled(activity, object : LocationServiceListener {
                override fun onLocationServiceResult(isEnabled: Boolean) {
                    result.success(isEnabled)
                }

                override fun onLocationServiceError(errorCode: ErrorCodes) {
                    result.error(errorCode.code, errorCode.description, null)
                }
            })
        } catch (e: Exception ){
            // todo should send report ?
            Log.e(TAG, "onIsLocationServiceEnable failed. Exception: $e")
            throw e
        }
    }

    private fun onGetAccuracyPermission(result: MethodChannel.Result) = try {
        val permission = PermissionManager.getAccuracyPermission(context)
        result.success(permission.ordinal)
    } catch (e: Exception) {
        Log.e(TAG,"onGetAccuracyPermission failed. Exception: $e")
        throw e
    }

    private fun onGetLastKnownPosition(call: MethodCall, result: MethodChannel.Result) {
        try {
            if (!PermissionManager.hasPermission(context)) {
                result.error(
                    ErrorCodes.permissionDenied.code,
                    ErrorCodes.permissionDenied.description,
                    null
                )
                return
            }
        } catch (e: PermissionUndefinedException) {
            val errorCode = ErrorCodes.permissionDefinitionsNotFound
            result.error(errorCode.code, errorCode.description, null)
            return
        }

        val forceLocationManager: Boolean = call.argument<Boolean>("forceLocationManager") == true
        SimpleBgLocationManager.getLastKnownPosition(context, forceLocationManager,
            positionChangedCallback = {
            val position: Position? = if (it != null) Position.fromLocation(it) else null
            result.success(position?.toMap())
        }, errorCallback = {
            result.error(it.code, it.description,null)
        })
    }

}