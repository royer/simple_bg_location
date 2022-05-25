package com.royzed.simple_bg_location

import android.util.Log
import androidx.annotation.NonNull
import com.royzed.simple_bg_location.permission.PermissionManager

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
    //TODO set methodCallHandler.activity
    activityPluginBinding = binding
    //TODO binding Service

    registerListeners()

  }


  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
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
