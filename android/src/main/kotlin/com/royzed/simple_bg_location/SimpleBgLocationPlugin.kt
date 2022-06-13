package com.royzed.simple_bg_location

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding



private const val TAG = "SimpleBgLocationPlugin"


/** SimpleBgLocationPlugin */
class SimpleBgLocationPlugin: FlutterPlugin, ActivityAware {


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onAttachedToFlutterEngine()")

    SimpleBgLocationModule.getInstance().onAttachedToEngine(
      flutterPluginBinding.applicationContext,
      flutterPluginBinding.binaryMessenger
    )

  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG,"onDetachedFromEngine()")
    SimpleBgLocationModule.getInstance().onDetachedFromEngine()
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(TAG, "onAttachedToActivity()")
    if (binding.activity is ComponentActivity) {
      Log.d(TAG,"activity is ComponentActivity")
    } else {
      Log.d(TAG, "activity is not ComponentActivity")
    }

    SimpleBgLocationModule.getInstance().onAttachedToActivity(binding)
  }


  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(TAG, "onDetachedFromActivityForConfigChanges()")
    SimpleBgLocationModule.getInstance().onDetachedFromActivityForConfigChanges()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(TAG, "onReattachedToActivityForConfigChanges()")
    SimpleBgLocationModule.getInstance().onReattachedToActivityForConfigChanges(binding)
  }

  override fun onDetachedFromActivity() {
    Log.d(TAG,"onDetachFromActivity()")
    SimpleBgLocationModule.getInstance().onDetachedFromActivity()
  }

  companion object {
    private const val PLUGIN_PATH = "com.royzed.simple_bg_location"
    const val METHOD_CHANNEL_NAME = "$PLUGIN_PATH/methods"
    const val EVENT_CHANNEL_PATH = "$PLUGIN_PATH/events"
  }

}

