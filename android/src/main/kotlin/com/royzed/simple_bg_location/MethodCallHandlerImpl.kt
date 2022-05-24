package com.royzed.simple_bg_location

import android.content.Context
import android.util.Log
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private const val TAG = "MethodCallHandlerImpl"

class MethodCallHandlerImpl : MethodChannel.MethodCallHandler {



    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var _channel : MethodChannel? = null
    private val channel : MethodChannel get() = _channel!!

    private lateinit var _context: Context
    private val context get() = _context!!


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else {
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
}