package com.royzed.simple_bg_location

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import com.royzed.simple_bg_location.callbacks.CallbacksManager
import com.royzed.simple_bg_location.callbacks.NotificationActionCallback
import com.royzed.simple_bg_location.callbacks.PositionCallback
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.domain.RequestOptions
import com.royzed.simple_bg_location.domain.location.LocationServiceListener
import com.royzed.simple_bg_location.domain.location.SimpleBgLocationManager
import com.royzed.simple_bg_location.domain.location.isLocationServiceEnable
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.errors.PermissionUndefinedException
import com.royzed.simple_bg_location.permission.BackgroundPermissionRationale
import com.royzed.simple_bg_location.permission.PermissionManager
import com.royzed.simple_bg_location.streams.NotificationActionStreamHandler
import com.royzed.simple_bg_location.streams.PositionStreamHandler
import com.royzed.simple_bg_location.utils.SettingsUtils
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class SimpleBgLocationModule : MethodChannel.MethodCallHandler {


    private lateinit var methodChannel: MethodChannel
    private lateinit var messenger: BinaryMessenger
    private lateinit var context: Context
    private var activityBinding: ActivityPluginBinding? = null
    private var activity: Activity? = null
    private val activityObserver: ActivityObserver = ActivityObserver()

    private val permissionManager: PermissionManager = PermissionManager()

    private val streamHandlers: MutableList<EventChannel.StreamHandler> = mutableListOf()

    val callbacksManager: CallbacksManager = CallbacksManager()

    private var _isReady: Boolean = false
    val isReady: Boolean get() = _isReady

    fun onAttachedToEngine(context: Context, messenger: BinaryMessenger) {
        this.messenger = messenger
        this.context = context
        methodChannel = MethodChannel(messenger, SimpleBgLocationPlugin.METHOD_CHANNEL_NAME)
        methodChannel.setMethodCallHandler(this)
    }

    fun onDetachedFromEngine() {
        methodChannel.setMethodCallHandler(null)
    }

    fun onAttachedToActivity(binding: ActivityPluginBinding) {
        assert(activity == null && activityBinding == null)
        activityBinding = binding
        activity = binding.activity
        activityObserver.attachActivity(activity!!)
        FlutterLifecycleAdapter.getActivityLifecycle(binding).addObserver(activityObserver)
        permissionManager.onAttachedToActivity(binding)

        synchronized(streamHandlers) {
            streamHandlers.add(PositionStreamHandler().register(context, messenger))
            streamHandlers.add(NotificationActionStreamHandler().register(context, messenger))
        }
    }


    fun onDetachedFromActivity() {
        assert(activity != null && activityBinding != null)

        permissionManager.onDetachedFromActivity()
        activityObserver.onDetachFromActivity()

        //FlutterLifecycleAdapter.getActivityLifecycle(activityBinding!!).removeObserver(activityObserver)

        unregisterAllListener()

        synchronized(streamHandlers) {
            streamHandlers.clear()
        }

        _isReady = false
        activityBinding = null
        activity = null
    }

    fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method) {
            Methods.checkPermission -> {
                onCheckPermission(result)
            }
            Methods.requestPermission -> {
                onRequestPermission(call, result)
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
            Methods.getCurrentPosition -> {
                onGetCurrentPosition(call, result)
            }
            Methods.openAppSettings -> {
                val hasOpenAppSettings = SettingsUtils.openAppSettings(context)
                result.success(hasOpenAppSettings)
            }
            Methods.openLocationSettings -> {
                val hasOpenLocationSettings = SettingsUtils.openLocationSettings(context)
                result.success(hasOpenLocationSettings)
            }
            Methods.requestPositionUpdate -> {
                onRequestPositionUpdate(call, result)
            }
            Methods.stopPositionUpdate -> {
                onStopPositionUpdate(result)
            }
            Methods.ready -> {
                onReady(result)
            }
            Methods.isPowerSaveMode -> {
                onIsPowerSaveMode(result)
            }
            else -> {
                result.notImplemented()
            }
        }
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

    private fun onRequestPermission(call: MethodCall, result: MethodChannel.Result) {
        try {
            val rationale = BackgroundPermissionRationale.fromMap(call.arguments())
            permissionManager.requestPermission(rationale, {
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
            SimpleBgLocationManager.isLocationServiceEnabled(context, object :
                LocationServiceListener {
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
        if (!hasPermission(result)) return

        val forceLocationManager: Boolean = call.argument<Boolean>("forceLocationManager") == true
        SimpleBgLocationManager.getLastKnownPosition(context, forceLocationManager,
            positionChangedCallback = {
                val position: Position? = if (it != null) Position.fromLocation(it) else null
                result.success(position?.toMap())
            }, errorCallback = {
                result.error(it.code, it.description,null)
            })
    }

    private fun onGetCurrentPosition(call: MethodCall, result: MethodChannel.Result) {

        if (!hasPermission(result)) return

        val forceLocationManager: Boolean = call.argument<Boolean>("forceLocationManager") == true
        activityObserver.getCurrentPosition(forceLocationManager, {
            val position: Position? = if (it != null) Position.fromLocation(it) else null
            result.success(position?.toMap())
        }, {
            result.error(it.code, it.description, null)
        })

    }

    private fun onRequestPositionUpdate(call: MethodCall, result: MethodChannel.Result) {
        if (!hasPermission(result)) {
            return
        }
        if (!isLocationServiceEnable(context)) {
            result.error(ErrorCodes.locationServicesDisabled.code,ErrorCodes.locationServicesDisabled.description, null)
            return
        }

        val requestOptions: RequestOptions = RequestOptions.fromMap(context, call.arguments())
        // Log.d(TAG,"onRequestPositionUpdate: $requestOptions")
        activityObserver.requestPositionUpdate(requestOptions)
        result.success(true)
    }

    private fun onStopPositionUpdate(result: MethodChannel.Result) {
        activityObserver.stopPositionUpdate()
        result.success(true)
    }

    private fun onReady(result: MethodChannel.Result) {
        _isReady = true
        val state = activityObserver.getState()
        Log.d(TAG,"onReady, state: $state")
        result.success(state.toMap())

    }

    private fun onIsPowerSaveMode(result: MethodChannel.Result) {
        val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result.success(powerManager.isPowerSaveMode)
        } else {
            result.success(true)
        }
    }


    private fun hasPermission(result: MethodChannel.Result): Boolean {
        try {
            if (!PermissionManager.hasPermission(context)) {
                result.error(
                    ErrorCodes.permissionDenied.code,
                    ErrorCodes.permissionDenied.description,
                    null
                )
                return false
            }
        } catch (e: PermissionUndefinedException) {
            val errorCode = ErrorCodes.permissionDefinitionsNotFound
            result.error(errorCode.code, errorCode.description, null)
            return false
        }
        return true
    }



    fun registerPositionListener(callback: PositionCallback) {
        callbacksManager.registerPositionListener(callback)
    }

    fun dispatchPositionEvent(position: Position) {
        if (isReady) {
            callbacksManager.dispatchPostionEvent(position)
        }
    }
    fun dispatchPositionErrorEvent(errorCode: ErrorCodes) {
        if (isReady) {
            callbacksManager.dispatchPositionErrorEvent(errorCode)
        }
    }

    fun registerNotificationActionListener(callback: NotificationActionCallback) {
        callbacksManager.registerNotificationActionListener(callback)
    }
    fun dispatchNotificationActionEvent(action: String) {
        callbacksManager.dispatchNotificationAction(action)
    }

    fun unregisterListener(eventName: String, callback: Any) {
        callbacksManager.unregisterListener(eventName, callback)
    }

    fun unregisterAllListener() {
        callbacksManager.unregisterAll()
    }


    companion object {
        private const val TAG = "SimpleBgLocationModule"

        @Volatile
        private var INSTANCE: SimpleBgLocationModule? = null

        fun getInstance(): SimpleBgLocationModule {
            return INSTANCE ?: synchronized(this) {
                val instance = SimpleBgLocationModule()
                INSTANCE = instance
                instance
            }
        }
    }

}