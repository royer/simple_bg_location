package com.royzed.simple_bg_location

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.royzed.simple_bg_location.permission.PermissionManager
import io.flutter.embedding.android.FlutterActivity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


private const val TAG = "SimpleBgLocationPlugin"

/** SimpleBgLocationPlugin */
class SimpleBgLocationPlugin: FlutterPlugin, ActivityAware {

  private lateinit var methodCallHandler: MethodCallHandlerImpl

  private val permissionManager: PermissionManager = PermissionManager()

  private var activityPluginBinding: ActivityPluginBinding? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onAttachedToFlutterEngine()")

    methodCallHandler = MethodCallHandlerImpl(permissionManager)
    methodCallHandler.startListening(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)

  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodCallHandler.stopListening()
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(TAG, "onAttachedToActivity()")
    if (binding.activity is ComponentActivity) {
      Log.d(TAG,"activity is ComponentActivity")
    } else {
      Log.d(TAG, "activity is not ComponentActivity")
    }
    methodCallHandler.setActivity(binding.activity)
    activityPluginBinding = binding
    //TODO binding Service

    registerListeners()

  }


  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(TAG, "onDetachedFromActivityForConfigChanges()")
    onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(TAG, "onReattachedToActivityForConfigChanges()")
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    Log.d(TAG,"onDetachedFromActivity()")
    dispose()
    unregisterListeners()

    if (activityPluginBinding != null) {
      // TODO "unbindService"
      activityPluginBinding = null
    }
  }

  private fun registerListeners() {
    if (activityPluginBinding != null) {
      activityPluginBinding!!.addRequestPermissionsResultListener(permissionManager)
    } else {
      Log.d(TAG, "SHOULDN'T -> registerListeners() activityPluginBinding = null!" )
    }
  }

  private fun unregisterListeners() {
    if (activityPluginBinding != null) {
      activityPluginBinding!!.removeRequestPermissionsResultListener(permissionManager)
    } else {
      Log.d(TAG, "SHOULDN'T -> unregisterListeners() activityPluginBinding = null!")
    }
  }

  private fun dispose() {

  }

}
