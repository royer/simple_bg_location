package com.royzed.simple_bg_location

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** SimpleBgLocationPlugin */
class SimpleBgLocationPlugin: FlutterPlugin {

  private lateinit var methodCallHandler: MethodCallHandlerImpl

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

    methodCallHandler = MethodCallHandlerImpl()
    methodCallHandler.startListening(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)

  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodCallHandler.stopListening()
  }
}
