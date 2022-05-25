package com.royzed.simple_bg_location

import android.content.Context
import android.util.Log
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
import com.royzed.simple_bg_location.permission.PermissionManager
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private const val TAG = "MethodCallHandlerImpl"

class MethodCallHandlerImpl(
    val permissionManager: PermissionManager
) : MethodChannel.MethodCallHandler {



    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var _channel : MethodChannel? = null
    private val channel : MethodChannel get() = _channel!!

    private var _context: Context? = null
    private val context get() = _context!!


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == Methods.checkPermission)  {
            onCheckPermission(result)
        }
        else {
            result.notImplemented()
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
            val permission = permissionManager.checkPermissionStatus(context)
            result.success(permission.ordinal)

        } catch (e: PermissionUndefinedException) {
            Log.e(TAG,"onCheckPermission() got Exception: $e")
            val errorCode = ErrorCodes.permissionDefinitionsNotFound
            result.error(errorCode.code, errorCode.description, null)
        }
    }



}