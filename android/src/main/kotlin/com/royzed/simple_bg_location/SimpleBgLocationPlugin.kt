package com.royzed.simple_bg_location

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.royzed.simple_bg_location.permission.PermissionManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter

private const val TAG = "SimpleBgLocationPlugin"

/** SimpleBgLocationPlugin */
class SimpleBgLocationPlugin: FlutterPlugin, ActivityAware {

  private lateinit var methodCallHandler: MethodCallHandlerImpl

  private val permissionManager: PermissionManager = PermissionManager()

  private var activityPluginBinding: ActivityPluginBinding? = null

  private val activityObserver: ActivityObserver = ActivityObserver()

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onAttachedToFlutterEngine()")

    methodCallHandler = MethodCallHandlerImpl(permissionManager, activityObserver)
    methodCallHandler.startListening(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)

  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onDetachedFromEngine()")
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
    permissionManager.onAttachedToActivity(binding)

    // (binding.lifecycle as HiddenLifecycleReference).lifecycle.addObserver(activityObserver)
    FlutterLifecycleAdapter.getActivityLifecycle(binding).addObserver(activityObserver)
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
    permissionManager.onDetachedFromActivity()
    dispose()
    unregisterListeners()



    if (activityPluginBinding != null) {

      (activityPluginBinding!!.lifecycle as HiddenLifecycleReference).lifecycle.removeObserver(activityObserver)
      activityPluginBinding = null
    }
  }

  private fun registerListeners() {
    if (activityPluginBinding != null) {
      // register RequestPermissionResultListener move to PermissionManager
      //activityPluginBinding!!.addRequestPermissionsResultListener(permissionManager)
      activityPluginBinding!!.addOnSaveStateListener(activityObserver)
    } else {
      Log.d(TAG, "SHOULDN'T -> registerListeners() activityPluginBinding = null!" )
    }
  }

  private fun unregisterListeners() {
    if (activityPluginBinding != null) {
      // removeRequestPermissionsResultListener move to PermissionManager.onActivityDetached()
      // activityPluginBinding!!.removeRequestPermissionsResultListener(permissionManager)
      activityPluginBinding!!.removeOnSaveStateListener(activityObserver)
    } else {
      Log.d(TAG, "SHOULDN'T -> unregisterListeners() activityPluginBinding = null!")
    }
  }

  private fun dispose() {

  }

}

